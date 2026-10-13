package com.mcmp.gcpcollector.service;

import com.google.cloud.bigquery.*;
import com.mcmp.gcpcollector.config.BigQueryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingQueryService {

    private final BigQuery bigQuery;
    private final BigQueryConfig bigQueryConfig;

    /**
     * 특정 날짜의 raw 빌링 데이터 전체 조회 (배치 적재용)
     * date: "2026-02-19" 형식, 해당 날짜 하루치 데이터 반환
     */
    public List<Map<String, Object>> getRawBillingByDate(String date) {
        LocalDate parsedDate = LocalDate.parse(date);   // 형식 검증: 실패 시 DateTimeParseException
        String startDate = parsedDate.toString();
        String endDate   = parsedDate.plusDays(1).toString();
        String fullTable = getFullTableName();           // 설정값에서 오므로 신뢰 가능
        boolean detailed = isDetailedExport(bigQueryConfig.getTable());

        // 표준 빌링 내보내기 기준 필드. resource.* 는 상세(detailed) 내보내기(gcp_billing_export_resource_v1_*) 에만 존재하므로
        // 테이블명으로 판별해 상세 export 일 때만 SELECT 하고, 표준 export 는 NULL 로 채운다 (GCS 버킷명 매칭용, Task D).
        // WHERE 조건은 named parameter(@startDate, @endDate)로 바인딩 → SQL 인젝션 방지
        // 테이블명은 BigQuery 파라미터 미지원(DDL 제약) → 설정값에서 주입
        String query = """
                SELECT
                     billing_account_id
                    ,service.id          AS service_id
                    ,service.description AS service_description
                    ,sku.id              AS sku_id
                    ,sku.description     AS sku_description
                    ,usage_start_time
                    ,usage_end_time
                    ,project.id               AS project_id
                    ,project.number           AS project_number
                    ,project.name             AS project_name
                    ,project.ancestry_numbers AS project_ancestry_numbers
                    ,TO_JSON_STRING(labels)        AS labels
                    ,TO_JSON_STRING(system_labels) AS system_labels
                    ,location.location AS location
                    ,location.country  AS location_country
                    ,location.region   AS location_region
                    ,location.zone     AS location_zone
                    ,TO_JSON_STRING(tags) AS tags
                    ,cost
                    ,currency
                    ,currency_conversion_rate
                    ,usage.amount                  AS usage_amount
                    ,usage.unit                    AS usage_unit
                    ,usage.amount_in_pricing_units AS usage_amount_in_pricing_units
                    ,usage.pricing_unit            AS usage_pricing_unit
                    ,invoice.month AS invoice_month
                    ,cost_type
                    ,adjustment_info.id          AS adjustment_info_id
                    ,adjustment_info.description AS adjustment_info_description
                    ,adjustment_info.mode        AS adjustment_info_mode
                    ,adjustment_info.type        AS adjustment_info_type
                    ,export_time
                    %s
                FROM %s
                WHERE usage_start_time >= @startDate
                  AND usage_start_time <  @endDate
                """.formatted(resourceColumns(detailed), fullTable);

        log.info("BigQuery raw 조회 - date: {}, table: {}, detailedExport: {}", date, fullTable, detailed);

        TableResult result = executeQuery(query, startDate, endDate);
        List<Map<String, Object>> rows = mapResultToList(result);

        log.info("BigQuery raw 조회 완료 - date: {}, 건수: {}", date, rows.size());
        return rows;
    }

    /** 상세(detailed) 사용량 비용 내보내기 테이블인지 — 이름 규칙 gcp_billing_export_resource_v1_&lt;id&gt; */
    static boolean isDetailedExport(String tableName) {
        return tableName != null && tableName.toLowerCase().contains("resource");
    }

    /** 상세 export 면 resource.name / resource.global_name 을, 아니면 NULL 컬럼을 같은 별칭으로 SELECT */
    static String resourceColumns(boolean detailed) {
        return detailed
                ? ",resource.name        AS resource_name\n                    ,resource.global_name AS resource_global_name"
                : ",CAST(NULL AS STRING) AS resource_name\n                    ,CAST(NULL AS STRING) AS resource_global_name";
    }

    public String getFullTableName() {
        return String.format("`%s.%s.%s`",
                bigQueryConfig.getProjectId(),
                bigQueryConfig.getDataset(),
                bigQueryConfig.getTable());
    }

    private TableResult executeQuery(String query, String startDate, String endDate) {
        try {
            long startMicros = LocalDate.parse(startDate).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() * 1000L;
            long endMicros   = LocalDate.parse(endDate).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() * 1000L;
            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                    .setUseLegacySql(false)
                    .addNamedParameter("startDate", QueryParameterValue.timestamp(startMicros))
                    .addNamedParameter("endDate",   QueryParameterValue.timestamp(endMicros))
                    .build();
            return bigQuery.query(queryConfig);
        } catch (BigQueryException e) {
            int code = e.getCode();
            if (code == 403) {
                log.error("BigQuery 권한 없음 (403) - 서비스 계정에 'BigQuery Job User' 및 'BigQuery Data Viewer' 역할이 필요합니다. message: {}", e.getMessage());
            } else if (code == 404) {
                log.error("BigQuery 테이블 없음 (404) - 테이블을 찾을 수 없습니다. table: {}, message: {}", getFullTableName(), e.getMessage());
            } else {
                log.error("BigQuery 질의 실패 (code: {}) - message: {}", code, e.getMessage());
            }
            throw new RuntimeException("BigQuery 질의 실패 [" + code + "]: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("BigQuery 질의 중단됨", e);
        }
    }

    private List<Map<String, Object>> mapResultToList(TableResult result) {
        List<Map<String, Object>> rows = new ArrayList<>();
        FieldList fields = result.getSchema().getFields();

        for (FieldValueList row : result.iterateAll()) {
            Map<String, Object> rowMap = new LinkedHashMap<>();
            for (Field field : fields) {
                String name = field.getName();
                FieldValue value = row.get(name);

                if (value.isNull()) {
                    rowMap.put(name, null);
                    continue;
                }

                // 타입별 안전한 추출 (LegacySQLTypeName 활용)
                rowMap.put(name, switch (field.getType().getStandardType()) {
                    case FLOAT64, NUMERIC -> value.getDoubleValue();
                    case INT64 -> value.getLongValue();
                    case BOOL -> value.getBooleanValue();
                    case TIMESTAMP -> value.getTimestampValue();
                    default -> value.getStringValue();
                });
            }
            rows.add(rowMap);
        }
        return rows;
    }
}
