package com.mcmp.collector.service;

import com.mcmp.collector.dao.AwsDao;
import com.mcmp.collector.model.cur.AwsCurDetailModel;
import com.mcmp.collector.model.cur.AwsCurModel;
import com.mcmp.collector.model.cur.CurProcessModel;
import com.mcmp.collector.model.cur.RscGrpMetaModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.sts.model.StsException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class Cur {
    @Autowired
    private AssumeRole assumeRole;

    @Autowired
    private DateCalculator dateCalculator;

    @Autowired
    private AwsDao awsDao;

    @Value("${aws.data.export.name}")
    private String dataExportName;

    @Value("${aws.data.export.path.prefix}")
    private String dataExportPathPrefix;

    private static final Region region = Region.AP_NORTHEAST_2;
    private static final int BATCH_SIZE = 50;
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

    public void batchInsertCURData(String payerId, LocalDateTime collectDt, String seq, String todoCollectMonth, String preObjectKey, int run_idx){
        try{
            String bucketNM = assumeRole.getDataExportBucketNM(payerId);

            S3Object s3Object = null;
            try {
                s3Object = getFileKey(payerId, bucketNM, todoCollectMonth);

                if(Objects.equals(preObjectKey, s3Object.key()) || s3Object == null){
                    log.warn("No new cur data is created - Payer : " + payerId + ", Month : " + todoCollectMonth);
                    return;
                }
            } catch (Exception e){
                e.printStackTrace();
                log.warn("No data for that month - Payer : " + payerId + ", Month : " + todoCollectMonth);
                return;
            }

            ResponseInputStream<GetObjectResponse> gzObject = getCsvGzObject(payerId, bucketNM, s3Object.key());

            String line;
            List<AwsCurModel> batchList = new ArrayList<>();
            Map<String, Integer> headerMap = new HashMap<>();

            try(InputStream gzipInputStream = new GZIPInputStream(gzObject);
                BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream))){

                String headerLine = reader.readLine();
                if (headerLine != null) {
                    String[] headers = headerLine.split(",");
                    for (int i = 0; i < headers.length; i++) {
                        headerMap.put(headers[i].trim(), i);
                    }
                }

                String[] keys = {
                        "lineItem/UsageAccountId", "lineItem/ProductCode", "lineItem/ResourceId",
                        "lineItem/LineItemType", "product/instanceType", "pricing/unit",
                        "lineItem/UsageAmount", "lineItem/UnblendedCost", "lineItem/BlendedCost",
                        "lineItem/UsageStartDate", "lineItem/UsageEndDate", "pricing/publicOnDemandCost",
                        "pricing/publicOnDemandRate", "lineItem/CurrencyCode", "bill/InvoiceId", "product/sku", "product/region",
                        "product/instanceFamily", "product/location", "lineItem/Operation",
                        "product/instanceTypeFamily", "lineItem/UsageType", "product/vcpu", "product/memory"
                };
                Map<String, Integer> indexMap = Arrays.stream(keys)
                        .collect(Collectors.toMap(key -> key, key -> headerMap.getOrDefault(key, -1)));
                boolean allColumnsPresent = indexMap.values().stream().noneMatch(index -> index == -1);
                if (!allColumnsPresent) {
                    log.error("Cur Batch Insert - One or more required headers are missing. Stop processing your data.");
                    return;
                }

                if(run_idx == 0){
                    awsDao.dropTable(todoCollectMonth);
                    awsDao.createTable(todoCollectMonth);
                }

                String certifed_fixed_yn = "Y";

                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(",", -1);
                    OffsetDateTime usageStartDt = OffsetDateTime.parse(fields[indexMap.get("lineItem/UsageStartDate")], dtFormatter);
                    OffsetDateTime usageEndDt = OffsetDateTime.parse(fields[indexMap.get("lineItem/UsageEndDate")], dtFormatter);

                    if(fields[indexMap.get("bill/InvoiceId")].isEmpty()){
                        certifed_fixed_yn = "N";
                    }

                    AwsCurModel usageData = new AwsCurModel(
                            fields[indexMap.get("lineItem/UsageAccountId")],
                            fields[indexMap.get("lineItem/ProductCode")],
                            fields[indexMap.get("lineItem/ResourceId")],
                            fields[indexMap.get("lineItem/LineItemType")],
                            fields[indexMap.get("product/instanceType")],
                            fields[indexMap.get("pricing/unit")],
                            fields[indexMap.get("lineItem/UsageAmount")],
                            fields[indexMap.get("lineItem/UnblendedCost")],
                            fields[indexMap.get("lineItem/BlendedCost")],
                            usageStartDt.toLocalDateTime(),
                            usageEndDt.toLocalDateTime(),
                            fields[indexMap.get("pricing/publicOnDemandCost")],
                            fields[indexMap.get("pricing/publicOnDemandRate")],
                            fields[indexMap.get("lineItem/CurrencyCode")],
                            collectDt,
                            seq,
                            fields[indexMap.get("product/sku")],
                            fields[indexMap.get("product/region")],
                            fields[indexMap.get("product/instanceFamily")],
                            fields[indexMap.get("product/location")],
                            fields[indexMap.get("lineItem/Operation")],
                            fields[indexMap.get("product/instanceTypeFamily")],
                            fields[indexMap.get("lineItem/UsageType")],
                            fields[indexMap.get("product/vcpu")],
                            fields[indexMap.get("product/memory")]
                    );

                    batchList.add(usageData);

                    if (batchList.size() == BATCH_SIZE) {
                        try{
                            insertOriginBatch(batchList);

                            AwsCurDetailModel detailModel = new AwsCurDetailModel();
                            detailModel.setSuffix(todoCollectMonth);
                            detailModel.setBatchList(batchList);
                            insertDetailBatch(detailModel);
                        } catch (Exception e){
                            log.error("Error - Insert CUR Batch Data - Account : " + payerId);
                            e.printStackTrace();
                        }
                        batchList.clear();
                    }
                }

                // Insert remaining records
                if (!batchList.isEmpty()) {
                    insertOriginBatch(batchList);
                    AwsCurDetailModel detailModel = new AwsCurDetailModel();
                    detailModel.setSuffix(todoCollectMonth);
                    detailModel.setBatchList(batchList);
                    insertDetailBatch(detailModel);
                }

                CurProcessModel updateProcess = CurProcessModel.builder()
                        .csp("AWS")
                        .payer_account(payerId)
                        .collect_date(todoCollectMonth)
                        .certifed_fixed_yn(certifed_fixed_yn)
                        .object_key(s3Object.key())
                        .build();
                if(certifed_fixed_yn.equals("Y")){
                    updateProcess.setCertifed_fixed_date(collectDt);
                }

                RscGrpMetaModel rscGrpMetaModel = RscGrpMetaModel.builder()
                        .csp("AWS")
                        .account(payerId)
                        .prj_cd("undefined")
                        .year_month(todoCollectMonth)
                        .build();

                awsDao.insertCURProcess(updateProcess);
                awsDao.insertRscGrpMeta(rscGrpMetaModel);

                calMonthlySum(todoCollectMonth);
                calDailySumByProduct(todoCollectMonth);

            }

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void insertOriginBatch(List<AwsCurModel> batchList) {
        awsDao.insertCurOriginBatch(batchList);
    }

    private void insertDetailBatch(AwsCurDetailModel model){
        awsDao.insertCurDetailBatch(model);
    }

    public void getS3BucketNames(String payerId){
        StaticCredentialsProvider staticCredentialsProvider = assumeRole.assumeRole(payerId);

        try(S3Client s3 = S3Client.builder()
                .credentialsProvider(staticCredentialsProvider)
                .region(region)
                .build()){
            List<Bucket> buckets = s3.listBuckets().buckets();
            for(Bucket bucket : buckets){
//                System.out.println("bucket name: " + bucket.name());
            }
        }
    }

    public S3Object getFileKey(String payerId, String bucketNM, String todoCollectMonth){
        StaticCredentialsProvider staticCredentialsProvider = assumeRole.assumeRole(payerId);

        Optional<S3Object> latestObject = Optional.empty();

        try(S3Client s3Client = S3Client.builder()
                .credentialsProvider(staticCredentialsProvider)
                .region(region)
                .build()){

            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketNM)
                    .prefix(dataExportPathPrefix + "/" + dataExportName + "/" + dateCalculator.getMonthRange(todoCollectMonth, 1) + "/")
                    .build();

            ListObjectsV2Response listObjectsResponse;
            do {
                listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

                for (S3Object s3Object : listObjectsResponse.contents()) {
                    if ((!latestObject.isPresent() || s3Object.lastModified().isAfter(latestObject.get().lastModified())) && s3Object.key().contains(".csv")) {
                        latestObject = Optional.of(s3Object);
                    }
                }

                listObjectsRequest = listObjectsRequest.toBuilder()
                        .continuationToken(listObjectsResponse.nextContinuationToken())
                        .build();

            } while (listObjectsResponse.isTruncated());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return latestObject.orElseThrow(() -> new RuntimeException("No CUR Objects found"));
    }

    public ResponseInputStream<GetObjectResponse> getCsvGzObject(String payerId, String bucketNM, String key){
        StaticCredentialsProvider staticCredentialsProvider = assumeRole.assumeRole(payerId);

        try{
            S3Client s3Client = S3Client.builder()
                    .credentialsProvider(staticCredentialsProvider)
                    .region(region)
                    .build();

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketNM)
                    .key(key)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(objectRequest);

            return s3Object;

        } catch (StsException e){
            e.printStackTrace();
        }

        throw new RuntimeException();
    }

    // Simple Duplicate s3 object to Local
    public void getObjectBytes(String payerId){
        StaticCredentialsProvider staticCredentialsProvider = assumeRole.assumeRole(payerId);

        try{
            S3Client s3Client = S3Client.builder()
                    .credentialsProvider(staticCredentialsProvider)
                    .region(region)
                    .build();

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket("mcmp-costopti-curbucket")
                    .key("s3 테스트 파일.txt")
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            File file = new File("C:\\txt");
            OutputStream os = new FileOutputStream(file);
            os.write(data);
            System.out.println("Successfully");
            os.close();

        } catch (StsException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void calMonthlySum(String yearmonth){
        awsDao.insertMonthlySum(yearmonth);
    }

    public void calDailySumByProduct(String yearmonth){
        awsDao.insertDailySumByProduct(yearmonth);
    }

}
