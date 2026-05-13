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

@Slf4j
public class GcpBillingItemProcessor implements ItemProcessor<Map<String, Object>, GcpBillingRawDto> {

    private static final DateTimeFormatter BQ_TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS z"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            .toFormatter();

    @Override
    public GcpBillingRawDto process(Map<String, Object> row) {
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
                .labels(str(row, "labels"))
                .systemLabels(str(row, "system_labels"))
                .tags(str(row, "tags"))
                .build();
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
