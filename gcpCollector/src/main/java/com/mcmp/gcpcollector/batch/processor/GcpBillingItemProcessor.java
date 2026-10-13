package com.mcmp.gcpcollector.batch.processor;

import com.mcmp.gcpcollector.dto.GcpBillingRawDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GcpBillingItemProcessor implements ItemProcessor<Map<String, Object>, GcpBillingRawDto> {

    private static final DateTimeFormatter BQ_TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS z"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            .toFormatter();

    private static final String SERVICE_GCS = "Cloud Storage";
    private static final String LABEL_GKE_CLUSTER = "goog-k8s-cluster-name";

    @Override
    public GcpBillingRawDto process(Map<String, Object> row) {
        String labels = str(row, "labels");
        String systemLabels = str(row, "system_labels");
        String serviceDescription = str(row, "service_description");

        // Tumblebug 메타(servicegroup_meta.csp_instanceid) 매칭 키
        //  1) GCE VM: Tumblebug 이 붙인 sys.cspResourceId 라벨
        //  2) GKE  : GCP 가 붙이는 goog-k8s-cluster-name 라벨(labels 또는 system_labels) = 클러스터명 = Tumblebug cspResourceId. 관리비·노드 VM 모두
        //  3) GCS  : 상세 export 의 resource.global_name(//storage.googleapis.com/projects/_/buckets/<bucket>) 또는 resource.name = 버킷명
        String k8sClusterName = firstNonBlank(labelValue(labels, LABEL_GKE_CLUSTER), labelValue(systemLabels, LABEL_GKE_CLUSTER));
        String resourceName = str(row, "resource_name");
        String bucketName = bucketFromGlobalName(str(row, "resource_global_name"));
        String matchKey = resolveMatchKey(labelValue(labels, "sys_cspresourceid"), serviceDescription, k8sClusterName, bucketName, resourceName);

        return GcpBillingRawDto.builder()
                .billingAccountId(str(row, "billing_account_id"))
                .cost(dbl(row, "cost"))
                .costType(str(row, "cost_type"))
                .currency(str(row, "currency"))
                .currencyConversionRate(dbl(row, "currency_conversion_rate"))
                .exportTime(timestamp(row, "export_time"))
                .invoiceMonth(str(row, "invoice_month"))
                .serviceId(str(row, "service_id"))
                .serviceDescription(serviceDescription)
                .skuId(str(row, "sku_id"))
                .skuDescription(str(row, "sku_description"))
                .projectId(str(row, "project_id"))
                .projectNumber(str(row, "project_number"))
                .projectName(str(row, "project_name"))
                .projectAncestryNumbers(str(row, "project_ancestry_numbers"))
                .location(str(row, "location"))
                .locationCountry(str(row, "location_country"))
                .locationRegion(str(row, "location_region"))
                .locationZone(str(row, "location_zone"))
                .usageStartTime(timestamp(row, "usage_start_time"))
                .usageEndTime(timestamp(row, "usage_end_time"))
                .usageAmount(dbl(row, "usage_amount"))
                .usageUnit(str(row, "usage_unit"))
                .usageAmountInPricingUnits(dbl(row, "usage_amount_in_pricing_units"))
                .usagePricingUnit(str(row, "usage_pricing_unit"))
                .adjustmentInfoId(str(row, "adjustment_info_id"))
                .adjustmentInfoDescription(str(row, "adjustment_info_description"))
                .adjustmentInfoMode(str(row, "adjustment_info_mode"))
                .adjustmentInfoType(str(row, "adjustment_info_type"))
                .labels(labels)
                .systemLabels(systemLabels)
                .tags(str(row, "tags"))
                // labels(sys.* JSON)에서 servicegroup_meta 매핑용 식별자 추출 (+ GKE/GCS 매칭 키 폴백)
                .cspInstanceid(matchKey)
                .vmId(labelValue(labels, "sys_id"))
                .mciId(labelValue(labels, "sys_infraid"))
                .serviceCd(labelValue(labels, "sys_namespace"))
                .k8sClusterName(k8sClusterName)
                .resourceName(resourceName)
                .build();
    }

    /**
     * 매칭 키 결정. sys_cspresourceid 가 있으면 그대로(기존 동작), 없으면
     * goog-k8s-cluster-name 라벨이 있는 행 → 클러스터명 (서비스 무관: Kubernetes Engine 관리비와 Compute Engine 노드 VM 을 같은 클러스터로 합산),
     * Cloud Storage → 버킷명(global_name 우선, 없으면 resource.name). 그 외는 null.
     */
    static String resolveMatchKey(String sysCspResourceId, String serviceDescription, String k8sClusterName, String bucketName, String resourceName) {
        if (!isBlank(sysCspResourceId)) return sysCspResourceId;
        if (!isBlank(k8sClusterName)) return k8sClusterName;
        if (SERVICE_GCS.equals(serviceDescription)) return firstNonBlank(bucketName, resourceName);
        return null;
    }

    /** "//storage.googleapis.com/projects/_/buckets/my-bucket" → "my-bucket". 형식이 다르면 null. */
    static String bucketFromGlobalName(String globalName) {
        if (isBlank(globalName)) return null;
        int idx = globalName.indexOf("/buckets/");
        if (idx < 0) return null;
        String rest = globalName.substring(idx + "/buckets/".length());
        int slash = rest.indexOf('/');
        String bucket = slash >= 0 ? rest.substring(0, slash) : rest;
        return isBlank(bucket) ? null : bucket;
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) if (!isBlank(v)) return v;
        return null;
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    // labels 형식: [{"key":"sys_cspresourceid","value":"tb53..."}, ...]  (system_labels 도 동일 형식)
    // GCP 빌링은 sys.cspResourceId → sys_cspresourceid 로 평탄화되므로 키 변형(. _ - 대소문자)에 tolerant 하게 매칭
    static String labelValue(String labelsJson, String key) {
        if (labelsJson == null || labelsJson.isEmpty()) return null;
        String flexKey = key.replace("_", "[._-]?").replace("-", "[._-]?");
        Matcher m = Pattern
                .compile("\"key\"\\s*:\\s*\"(?i:" + flexKey + ")\"\\s*,\\s*\"value\"\\s*:\\s*\"([^\"]*)\"")
                .matcher(labelsJson);
        return m.find() ? m.group(1) : null;
    }

    private String str(Map<String, Object> row, String key) {
        Object val = row.get(key);
        return val != null ? val.toString() : null;
    }

    private Double dbl(Map<String, Object> row, String key) {
        Object val = row.get(key);
        if (val == null) return null;
        if (val instanceof Double) return (Double) val;
        try {
            return Double.parseDouble(val.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDateTime timestamp(Map<String, Object> row, String key) {
        Object val = row.get(key);
        if (val == null) return null;
        try {
            // BigQuery getTimestampValue()는 epoch microseconds 반환
            long epochMicros = Long.parseLong(val.toString());
            return LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(epochMicros / 1_000_000L, (epochMicros % 1_000_000L) * 1_000L),
                    ZoneOffset.UTC
            );
        } catch (NumberFormatException e) {
            try {
                return LocalDateTime.parse(val.toString(), BQ_TIMESTAMP_FORMATTER);
            } catch (Exception ex) {
                log.warn("timestamp 파싱 실패 - key: {}, value: {}", key, val);
                return null;
            }
        }
    }
}
