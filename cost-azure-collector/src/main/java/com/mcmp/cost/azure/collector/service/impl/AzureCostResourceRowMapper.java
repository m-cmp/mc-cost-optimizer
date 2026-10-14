package com.mcmp.cost.azure.collector.service.impl;

import com.azure.resourcemanager.costmanagement.models.QueryColumn;
import com.azure.resourcemanager.costmanagement.models.QueryResult;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
import com.mcmp.cost.azure.collector.utils.AzureResourceType;
import com.mcmp.cost.azure.collector.utils.AzureUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Cost Management QueryResult(자원 단위) → {@link AzureCostResourceDaily} 매핑. Azure 호출 없이 단위테스트 가능하도록 분리.
 * 컬럼은 고정 인덱스가 아니라 이름으로 해석한다(VM 경로의 row.get(n) 방식과 다름).
 */
@Slf4j
final class AzureCostResourceRowMapper {

    /** 비용 컬럼 이름 후보. EA/종량제 구독은 PreTaxCost, MCA 청구 계정은 Cost 로 온다. totalCost 는 집계 별칭으로 오는 경우 대비 */
    private static final List<String> COST_COLUMNS = List.of("PreTaxCost", "Cost", "totalCost");

    /** BackEnd SQL 은 금액을 KRW 로 보고 ${krwPerUsd} 로 나눈다 */
    private static final String EXPECTED_CURRENCY = "KRW";

    private AzureCostResourceRowMapper() {}

    static List<AzureCostResourceDaily> toEntities(QueryResult queryResult, AzureApiCredentialDto dto) {
        if (queryResult == null) return List.of();
        return toEntities(queryResult.columns(), queryResult.rows(), dto);
    }

    /** 여러 페이지의 row 를 합쳐서 매핑할 수 있도록 컬럼·row 를 직접 받는다. */
    static List<AzureCostResourceDaily> toEntities(List<QueryColumn> columns, List<List<Object>> rows, AzureApiCredentialDto dto) {
        if (rows == null) return List.of();
        Map<String, Integer> col = AzureUtils.columnIndex(columns);
        requireColumns(col, "UsageDate", "ResourceId", "Currency");
        String costColumn = COST_COLUMNS.stream().filter(col::containsKey).findFirst()
                .orElseThrow(() -> new IllegalStateException("Cost Management response lacks cost column " + COST_COLUMNS + " (columns=" + col.keySet() + ")"));

        Map<String, AzureCostResourceDaily> merged = new LinkedHashMap<>();   // key = resourceId(소문자)|usageDate
        int skipped = 0;
        int invalid = 0;
        TreeSet<String> otherCurrencies = new TreeSet<>();
        for (List<Object> row : rows) {
            try {
                // Cost Management 는 보통 소문자로 주지만 보장되지 않는다. DB 유니크 키는 대소문자를 무시하므로
                // 여기서도 소문자로 통일해야 같은 자원이 대소문자만 다른 두 행으로 들어가 saveAll 이 실패하지 않는다.
                String resourceId = str(row.get(col.get("ResourceId"))).toLowerCase(Locale.ROOT);
                Optional<AzureResourceType> type = AzureResourceType.fromResourceId(resourceId);
                if (type.isEmpty()) {
                    skipped++;
                    log.debug("skip non-target resource: {}", resourceId);
                    continue;
                }

                Object costValue = row.get(col.get(costColumn));
                if (!(costValue instanceof Number)) {
                    invalid++;
                    log.warn("skip row with non-numeric cost: resource={}, {}={}", resourceId, costColumn, costValue);
                    continue;
                }
                double cost = ((Number) costValue).doubleValue();
                String usageDate = usageDate(row.get(col.get("UsageDate")));
                String currency = str(row.get(col.get("Currency")));
                if (!EXPECTED_CURRENCY.equalsIgnoreCase(currency)) otherCurrencies.add(currency);

                String key = resourceId + "|" + usageDate;
                AzureCostResourceDaily dup = merged.get(key);
                if (dup != null) {                                                   // 동일 자원·일 복수 row → 합산
                    dup.setPreTaxCost(dup.getPreTaxCost() + cost);
                    continue;
                }

                String rg = col.containsKey("ResourceGroupName") ? str(row.get(col.get("ResourceGroupName"))) : "";
                String region = col.containsKey("ResourceLocation") ? str(row.get(col.get("ResourceLocation"))).toLowerCase(Locale.ROOT) : null;
                merged.put(key, AzureCostResourceDaily.builder()
                        .tenantId(dto.getTenantId())
                        .subscriptionId(dto.getSubscriptionId())
                        .preTaxCost(cost)
                        .usageDate(usageDate)
                        .resourceGroupName(rg.isEmpty() ? resourceGroupFromId(resourceId) : rg)
                        .resourceId(resourceId)
                        .resourceName(resourceId.substring(resourceId.lastIndexOf('/') + 1))
                        .resourceType(type.get().name())
                        .serviceName(type.get().getServiceName())
                        .region(region == null || region.isEmpty() ? null : region)
                        .currency(currency)
                        .build());
            } catch (RuntimeException e) {
                // 형식이 이상한 행 하나 때문에 전체 결과를 버리지 않는다
                invalid++;
                log.warn("skip malformed Azure resource cost row: {} ({})", row, e.getMessage());
            }
        }
        if (skipped > 0) log.info("Azure resource cost: skipped {} rows outside target types", skipped);
        if (invalid > 0) log.warn("Azure resource cost: skipped {} malformed rows", invalid);
        if (!otherCurrencies.isEmpty()) {
            log.warn("Azure resource cost: currency {} found but BackEnd converts amounts as {} (KRW/USD rate). Amounts on screen will be wrong for these rows",
                    otherCurrencies, EXPECTED_CURRENCY);
        }
        return new ArrayList<>(merged.values());
    }

    private static void requireColumns(Map<String, Integer> col, String... names) {
        for (String n : names) {
            if (!col.containsKey(n)) throw new IllegalStateException("Cost Management response lacks column: " + n + " (columns=" + col.keySet() + ")");
        }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }

    /** UsageDate 는 20250903 같은 숫자로 오므로 toString 시 지수표기/소수점이 섞이지 않게 정수 변환 */
    private static String usageDate(Object o) { return (o instanceof Number n) ? String.valueOf(n.longValue()) : str(o); }

    /** /subscriptions/x/resourcegroups/{rg}/providers/... → rg */
    static String resourceGroupFromId(String id) {
        String[] seg = id.split("/");
        for (int i = 0; i < seg.length - 1; i++) {
            if ("resourcegroups".equalsIgnoreCase(seg[i])) return seg[i + 1];
        }
        return "";
    }
}
