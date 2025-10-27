package com.mcmp.cost.azure.collector.utils;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.costmanagement.models.ExportType;
import com.azure.resourcemanager.costmanagement.models.FunctionType;
import com.azure.resourcemanager.costmanagement.models.GranularityType;
import com.azure.resourcemanager.costmanagement.models.QueryAggregation;
import com.azure.resourcemanager.costmanagement.models.QueryColumnType;
import com.azure.resourcemanager.costmanagement.models.QueryComparisonExpression;
import com.azure.resourcemanager.costmanagement.models.QueryDataset;
import com.azure.resourcemanager.costmanagement.models.QueryDefinition;
import com.azure.resourcemanager.costmanagement.models.QueryFilter;
import com.azure.resourcemanager.costmanagement.models.QueryGrouping;
import com.azure.resourcemanager.costmanagement.models.QueryOperatorType;
import com.azure.resourcemanager.costmanagement.models.QueryTimePeriod;
import com.azure.resourcemanager.costmanagement.models.TimeframeType;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

public class AzureUtils {

    private AzureUtils() {
        throw new IllegalStateException("Cannot instantiate a utility class.");
    }

    public static ClientSecretCredential buildCredential(AzureApiCredentialDto azureApiCredentialDto) {
        return new ClientSecretCredentialBuilder()
                .tenantId(azureApiCredentialDto.getTenantId())
                .clientId(azureApiCredentialDto.getClientId())
                .clientSecret(azureApiCredentialDto.getClientSecret())
                .build();
    }

    public static AzureProfile buildProfile(AzureApiCredentialDto azureApiCredentialDto) {
        return new AzureProfile(azureApiCredentialDto.getTenantId(),
                azureApiCredentialDto.getSubscriptionId(),
                AzureEnvironment.AZURE);
    }

    public static QueryDefinition getQueryCostByServieName() {
        // 어제 날짜 구하기
        LocalDate today = LocalDate.now().minusDays(1);
        // 시작 시간: 00:00:00
        OffsetDateTime startOfDay = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        // 끝 시간: 23:59:59
        OffsetDateTime endOfDay = today.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        return new QueryDefinition()
                .withType(ExportType.ACTUAL_COST)
                .withTimeframe(TimeframeType.CUSTOM)
                .withTimePeriod(new QueryTimePeriod()
                        .withFrom(startOfDay)
                        .withTo(endOfDay))
                .withDataset(new QueryDataset()
                        .withGranularity(GranularityType.DAILY)
                        .withAggregation(
                                Map.of(
                                        "totalCost",
                                        new QueryAggregation()
                                                .withName("PreTaxCost")
                                                .withFunction(FunctionType.SUM)
                                )
                        )
                        .withGrouping(List.of(
                                new QueryGrouping()
                                        .withType(QueryColumnType.DIMENSION)
                                        .withName("ServiceName")
                        ))
                );
    }

    public static QueryDefinition getQueryCostByVirtualMachines() {
        // 어제 날짜 구하기
        LocalDate today = LocalDate.now().minusDays(1);
        // 시작 시간: 00:00:00
        OffsetDateTime startOfDay = today.atStartOfDay().atOffset(ZoneOffset.UTC);
        // 끝 시간: 23:59:59
        OffsetDateTime endOfDay = today.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        return new QueryDefinition()
                .withType(ExportType.ACTUAL_COST)
                .withTimeframe(TimeframeType.CUSTOM)
                .withTimePeriod(new QueryTimePeriod()
                        .withFrom(startOfDay)
                        .withTo(endOfDay))
                .withDataset(new QueryDataset()
                        .withGranularity(GranularityType.DAILY)
                        .withAggregation(
                                Map.of(
                                        "totalCost",
                                        new QueryAggregation()
                                                .withName("PreTaxCost")
                                                .withFunction(FunctionType.SUM)
                                )
                        )
                        .withGrouping(List.of(
                                new QueryGrouping()
                                        .withType(QueryColumnType.DIMENSION)
                                        .withName("ResourceGroupName"),
                                new QueryGrouping()
                                        .withType(QueryColumnType.DIMENSION)
                                        .withName("ResourceId"),
                                new QueryGrouping()
                                        .withType(QueryColumnType.DIMENSION)
                                        .withName("ResourceGuid")
                        ))
                        .withFilter(new QueryFilter()
                                .withDimensions(
                                        new QueryComparisonExpression()
                                                .withName("ServiceName")
                                                .withOperator(QueryOperatorType.IN)
                                                .withValues(List.of("Virtual Machines"))
                                ))
                );
    }
}
