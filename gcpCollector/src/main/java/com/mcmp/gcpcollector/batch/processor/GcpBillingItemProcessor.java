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

    @Override
    public GcpBillingRawDto process(Map<String, Object> row) {
        String labels = str(row, "labels");
        return GcpBillingRawDto.builder()
                .billingAccountId(str(row, "billing_account_id"))
                .cost(dbl(row, "cost"))
                .costType(str(row, "cost_type"))
                .currency(str(row, "currency"))
                .currencyConversionRate(dbl(row, "currency_conversion_rate"))
                .exportTime(timestamp(row, "export_time"))
                .invoiceMonth(str(row, "invoice_month"))
                .serviceId(str(row, "service_id"))
                .serviceDescription(str(row, "service_description"))
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
                .systemLabels(str(row, "system_labels"))
                .tags(str(row, "tags"))
                // labels(sys.* JSON)에서 servicegroup_meta 매핑용 식별자 추출
                .cspInstanceid(labelValue(labels, "sys_cspresourceid"))
                .vmId(labelValue(labels, "sys_id"))
                .mciId(labelValue(labels, "sys_infraid"))
                .serviceCd(labelValue(labels, "sys_namespace"))
                .build();
    }

    // labels 형식: [{"key":"sys_cspresourceid","value":"tb53..."}, ...]
    // GCP 빌링은 sys.cspResourceId → sys_cspresourceid 로 평탄화되므로 키 변형(. _ 대소문자)에 tolerant 하게 매칭
    private static String labelValue(String labelsJson, String key) {
        if (labelsJson == null || labelsJson.isEmpty()) return null;
        String flexKey = key.replace("_", "[._]?");
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
