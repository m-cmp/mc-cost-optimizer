package com.mcmp.costbe.cur.service;

import com.mcmp.costbe.archive.credential.OpenBaoClient;
import com.mcmp.costbe.cur.dao.CurSetupDao;
import com.mcmp.costbe.cur.dto.CurSetupResult;
import com.mcmp.costbe.cur.dto.CurSetupStatus;
import com.mcmp.costbe.cur.dto.StepResult;
import com.mcmp.costbe.cur.exception.CurSetupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.costandusagereport.CostAndUsageReportClient;
import software.amazon.awssdk.services.costandusagereport.model.*;
import software.amazon.awssdk.services.costandusagereport.model.AWSRegion;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.sts.StsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurSetupService {

    private final OpenBaoClient openBaoClient;
    private final CurSetupDao curSetupDao;

    private static final String IAM_USER = "mcmp-costopti";
    private static final String REPORT_NAME = "mcmp-costopti";
    private static final String S3_REGION = "ap-northeast-2";

    // accountId 단위 동시 실행 방지
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    private static final String CUR_INLINE_POLICY = """
            {
              "Version": "2012-10-17",
              "Statement": [{
                "Effect": "Allow",
                "Action": [
                  "cur:PutReportDefinition",
                  "cur:ModifyReportDefinition",
                  "cur:DescribeReportDefinitions",
                  "bcm-data-exports:CreateExport",
                  "bcm-data-exports:ListExports",
                  "organizations:DescribeOrganization"
                ],
                "Resource": "*"
              }]
            }
            """;

    public CurSetupResult setup() {
        synchronized (locks.computeIfAbsent("singleton", k -> new Object())) {
            return doSetup();
        }
    }

    public CurSetupStatus getStatus() {
        Map<String, String> creds = openBaoClient.readPath("cost/aws");
        boolean costCredsStored = creds.containsKey("AWS_ACCESS_KEY_ID")
                && !creds.get("AWS_ACCESS_KEY_ID").isBlank();
        boolean dbRegistered = curSetupDao.isDbRegistered();
        String mailReceiver = curSetupDao.getMailReceiver();
        String bucketName   = curSetupDao.getBucketName();
        return new CurSetupStatus(costCredsStored, dbRegistered, mailReceiver, bucketName, REPORT_NAME);
    }

    public void saveMailReceiver(String email) {
        log.info("[CurSetup] Saving mail receiver: {}", email);
        curSetupDao.upsertMailReceiver(email);
    }

    private CurSetupResult doSetup() {
        List<StepResult> steps = new ArrayList<>();
        log.info("[CurSetup] ===== START =====");

        // Step 0: OpenBao write 권한 probe
        if (!runStep(steps, "OpenBao permission check", () -> {
            log.info("[CurSetup] Probing OpenBao write permission at cost/aws-probe");
            openBaoClient.writePath("cost/aws-probe", Map.of("probe", "ok"));
            try {
                openBaoClient.deletePath("cost/aws-probe");
                log.info("[CurSetup] OpenBao write/delete OK");
            } catch (Exception e) {
                log.warn("[CurSetup] Probe cleanup failed (continuing): {}", e.getMessage());
            }
        })) return new CurSetupResult(null, REPORT_NAME, steps);

        // Step 1: root 키 읽기 + STS로 account ID 자동 조회
        String[] rootCredsAndAccount = runStepGet(steps, "Root key retrieval", () -> {
            log.info("[CurSetup] Reading root credentials from OpenBao secret/data/csp/aws");
            Map<String, String> c = openBaoClient.readCsp("aws");
            String k = c.get("AWS_ACCESS_KEY_ID");
            String s = c.get("AWS_SECRET_ACCESS_KEY");
            if (k == null || k.isBlank() || s == null || s.isBlank())
                throw new CurSetupException("No root credentials found in OpenBao. Please register the root key first.");
            log.info("[CurSetup] Root key found (keyId={}...)", k.substring(0, Math.min(8, k.length())));
            AwsCredentialsProvider p = StaticCredentialsProvider.create(AwsBasicCredentials.create(k, s));
            try (StsClient sts = StsClient.builder().region(Region.US_EAST_1).credentialsProvider(p).build()) {
                String accountId = sts.getCallerIdentity().account();
                log.info("[CurSetup] STS GetCallerIdentity OK — accountId={}", accountId);
                return new String[]{k, s, accountId};
            }
        });
        if (rootCredsAndAccount == null) return new CurSetupResult(null, REPORT_NAME, steps);

        String accountId = rootCredsAndAccount[2];
        String defaultBucketName = "mcmp-costopti-cur-" + accountId;

        AwsCredentialsProvider rootProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(rootCredsAndAccount[0], rootCredsAndAccount[1]));

        // Step 2: 기존 CUR 보고서 확인 → 버킷명 결정 (존재하면 해당 버킷 사용, 없으면 신규 생성 경로)
        String[] curCheckResult = runStepGet(steps, "CUR existing report check", () -> {
            log.info("[CurSetup] Checking for existing CUR report: {}", REPORT_NAME);
            try (CostAndUsageReportClient cur = CostAndUsageReportClient.builder()
                    .region(Region.US_EAST_1).credentialsProvider(rootProvider).build()) {
                ReportDefinition existing = null;
                String nextToken = null;
                do {
                    final String token = nextToken;
                    DescribeReportDefinitionsResponse resp = token == null
                            ? cur.describeReportDefinitions()
                            : cur.describeReportDefinitions(r -> r.nextToken(token));
                    existing = resp.reportDefinitions().stream()
                            .filter(r -> r.reportName().equals(REPORT_NAME))
                            .findFirst()
                            .orElse(existing);
                    nextToken = resp.nextToken();
                } while (existing == null && nextToken != null);

                if (existing != null) {
                    log.info("[CurSetup] Existing CUR report found, retaining bucket: {}", existing.s3Bucket());
                    return new String[]{existing.s3Bucket(), "exists"};
                } else {
                    log.info("[CurSetup] No existing CUR report, will create with bucket: {}", defaultBucketName);
                    return new String[]{defaultBucketName, "new"};
                }
            }
        });
        if (curCheckResult == null) return new CurSetupResult(defaultBucketName, REPORT_NAME, steps);

        String bucketName = curCheckResult[0];
        boolean curReportExists = "exists".equals(curCheckResult[1]);

        // Step 3: IAM 유저 생성 + 정책 + 키 발급
        String[] newKeys = runStepGet(steps, "IAM setup & key issuance", () -> {
            log.info("[CurSetup] Connecting to IAM (region=aws-global)");
            try (IamClient iam = IamClient.builder()
                    .region(Region.AWS_GLOBAL).credentialsProvider(rootProvider).build()) {

                try {
                    iam.createUser(r -> r.userName(IAM_USER));
                    log.info("[CurSetup] IAM user created: {}", IAM_USER);
                } catch (EntityAlreadyExistsException e) {
                    log.info("[CurSetup] IAM user already exists, skipping: {}", IAM_USER);
                }

                iam.attachUserPolicy(r -> r.userName(IAM_USER)
                        .policyArn("arn:aws:iam::aws:policy/AmazonS3FullAccess"));
                iam.putUserPolicy(r -> r.userName(IAM_USER)
                        .policyName("mcmp_cur_export").policyDocument(CUR_INLINE_POLICY));
                log.info("[CurSetup] Policies attached to {}", IAM_USER);

                ListAccessKeysResponse list = iam.listAccessKeys(r -> r.userName(IAM_USER));
                log.info("[CurSetup] Existing key count for {}: {}", IAM_USER, list.accessKeyMetadata().size());
                for (AccessKeyMetadata key : list.accessKeyMetadata()) {
                    iam.deleteAccessKey(r -> r.userName(IAM_USER).accessKeyId(key.accessKeyId()));
                    log.info("[CurSetup] Deleted key: {}", key.accessKeyId());
                }

                CreateAccessKeyResponse resp = iam.createAccessKey(r -> r.userName(IAM_USER));
                log.info("[CurSetup] New access key issued: {}", resp.accessKey().accessKeyId());
                return new String[]{resp.accessKey().accessKeyId(), resp.accessKey().secretAccessKey()};
            }
        });
        if (newKeys == null) return new CurSetupResult(bucketName, REPORT_NAME, steps);

        log.info("[CurSetup] Waiting 15s for IAM key propagation…");
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        AwsCredentialsProvider mcmpProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(newKeys[0], newKeys[1]));

        // Step 4: S3 버킷 생성 (결정된 버킷명 사용)
        if (!runStep(steps, "S3 bucket creation", () -> {
            log.info("[CurSetup] Connecting to S3 (region={}) for bucket: {}", S3_REGION, bucketName);
            try (S3Client s3 = S3Client.builder()
                    .region(Region.of(S3_REGION)).credentialsProvider(mcmpProvider).build()) {
                try {
                    s3.createBucket(r -> r.bucket(bucketName)
                            .createBucketConfiguration(c -> c.locationConstraint(S3_REGION)));
                    log.info("[CurSetup] S3 bucket created: {}", bucketName);
                } catch (BucketAlreadyOwnedByYouException e) {
                    log.info("[CurSetup] S3 bucket already exists (owned by this account), skipping: {}", bucketName);
                } catch (S3Exception e) {
                    if ("BucketAlreadyExists".equals(e.awsErrorDetails().errorCode())) {
                        throw new CurSetupException(
                                "Bucket '" + bucketName + "' is already owned by another AWS account. Check the account ID.");
                    }
                    throw e;
                }
                s3.putBucketPolicy(r -> r.bucket(bucketName)
                        .policy(buildBillingPolicy(accountId, bucketName)));
                log.info("[CurSetup] Billing policy applied to bucket: {}", bucketName);
            }
        })) return new CurSetupResult(bucketName, REPORT_NAME, steps);

        // Step 5: CUR 보고서 생성 (이미 존재하면 기존 유지 SKIP)
        if (curReportExists) {
            steps.add(StepResult.skip("CUR report creation", "existing report retained → s3://" + bucketName));
            log.info("[CurSetup] CUR report already exists, retaining as-is: {}", REPORT_NAME);
        } else {
            if (!runStep(steps, "CUR report creation", () -> {
                log.info("[CurSetup] Connecting to CUR (region=us-east-1) for report: {}", REPORT_NAME);
                try (CostAndUsageReportClient cur = CostAndUsageReportClient.builder()
                        .region(Region.US_EAST_1).credentialsProvider(mcmpProvider).build()) {
                    cur.putReportDefinition(r -> r.reportDefinition(d -> d
                            .reportName(REPORT_NAME)
                            .timeUnit(TimeUnit.DAILY)
                            .format(ReportFormat.TEXT_OR_CSV)
                            .compression(CompressionFormat.GZIP)
                            .additionalSchemaElements(SchemaElement.RESOURCES)
                            .s3Bucket(bucketName)
                            .s3Prefix(REPORT_NAME)
                            .s3Region(AWSRegion.AP_NORTHEAST_2)
                            .reportVersioning(ReportVersioning.CREATE_NEW_REPORT)));
                    log.info("[CurSetup] CUR report created: {} → s3://{}/{}", REPORT_NAME, bucketName, REPORT_NAME);
                }
            })) return new CurSetupResult(bucketName, REPORT_NAME, steps);
        }

        // Step 6: 전용 키 cost/aws 저장
        if (!runStep(steps, "Dedicated key storage (cost/aws)", () -> {
            log.info("[CurSetup] Writing mcmp-costopti key to OpenBao secret/data/cost/aws");
            openBaoClient.writePath("cost/aws", Map.of(
                    "AWS_ACCESS_KEY_ID", newKeys[0],
                    "AWS_SECRET_ACCESS_KEY", newKeys[1]));
            log.info("[CurSetup] Key stored in OpenBao: {}", newKeys[0]);
        })) return new CurSetupResult(bucketName, REPORT_NAME, steps);

        // Step 7: DB 등록
        runStep(steps, "DB registration", () -> {
            log.info("[CurSetup] Upserting CUR setup record to DB (bucketNm={})", bucketName);
            curSetupDao.upsertCurSetup(bucketName);
            curSetupDao.upsertUserInfo(accountId);
            log.info("[CurSetup] DB registration complete (bucket={}, accountId={})", bucketName, accountId);
        });

        boolean allOk = steps.stream().noneMatch(s -> "FAILED".equals(s.getStatus()));
        log.info("[CurSetup] ===== {} — bucket={}, report={} =====",
                allOk ? "COMPLETE" : "FAILED", bucketName, REPORT_NAME);
        return new CurSetupResult(bucketName, REPORT_NAME, steps);
    }

    /** 단계 실행. 성공 시 true, 실패 시 FAILED 기록 후 false. */
    private boolean runStep(List<StepResult> steps, String name, ThrowingRunnable action) {
        try {
            action.run();
            steps.add(StepResult.ok(name));
            return true;
        } catch (CurSetupException e) {
            steps.add(StepResult.failed(name, e.getMessage()));
            log.error("[CurSetup] {} 실패: {}", name, e.getMessage());
            return false;
        } catch (Exception e) {
            steps.add(StepResult.failed(name, e.getMessage()));
            log.error("[CurSetup] {} 오류: {}", name, e.getMessage(), e);
            return false;
        }
    }

    /** 값을 반환하는 단계 실행. 성공 시 값, 실패 시 null (steps에 FAILED 기록). */
    private <T> T runStepGet(List<StepResult> steps, String name, ThrowingSupplier<T> action) {
        try {
            T result = action.get();
            steps.add(StepResult.ok(name));
            return result;
        } catch (CurSetupException e) {
            steps.add(StepResult.failed(name, e.getMessage()));
            log.error("[CurSetup] {} 실패: {}", name, e.getMessage());
            return null;
        } catch (Exception e) {
            steps.add(StepResult.failed(name, e.getMessage()));
            log.error("[CurSetup] {} 오류: {}", name, e.getMessage(), e);
            return null;
        }
    }

    private String buildBillingPolicy(String accountId, String bucketName) {
        return String.format("""
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"Service": "billingreports.amazonaws.com"},
                      "Action": ["s3:GetBucketAcl", "s3:GetBucketPolicy"],
                      "Resource": "arn:aws:s3:::%s",
                      "Condition": {"StringEquals": {"aws:SourceAccount": "%s"}}
                    },
                    {
                      "Effect": "Allow",
                      "Principal": {"Service": "billingreports.amazonaws.com"},
                      "Action": "s3:PutObject",
                      "Resource": "arn:aws:s3:::%s/*",
                      "Condition": {"StringEquals": {"aws:SourceAccount": "%s"}}
                    }
                  ]
                }
                """, bucketName, accountId, bucketName, accountId);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
