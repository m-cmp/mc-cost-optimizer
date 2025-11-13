package com.mcmp.cost.azure.collector.utils;

import com.azure.core.http.HttpClient;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class AzureUtils {

    private AzureUtils() {
        throw new IllegalStateException("Cannot instantiate a utility class.");
    }

    /**
     * SSL 검증을 우회하는 HttpClient 생성
     * WARNING: 개발 환경 전용. 운영 환경에서는 적절한 인증서를 사용하세요.
     */
    private static HttpClient createInsecureHttpClient() {
        try {
            log.warn("Azure HttpClient configured with SSL verification DISABLED. This is for development only!");

            // Reactor Netty HttpClient with insecure SSL
            reactor.netty.http.client.HttpClient reactorClient = reactor.netty.http.client.HttpClient.create()
                    .secure(sslSpec -> {
                        try {
                            sslSpec.sslContext(
                                SslContextBuilder.forClient()
                                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                    .build()
                            );
                        } catch (Exception e) {
                            log.error("Failed to configure SSL context", e);
                        }
                    });

            return new NettyAsyncHttpClientBuilder(reactorClient).build();

        } catch (Exception e) {
            log.error("Failed to create insecure HttpClient, falling back to default", e);
            return new NettyAsyncHttpClientBuilder().build();
        }
    }

    public static ClientSecretCredential buildCredential(AzureApiCredentialDto azureApiCredentialDto) {
        return buildCredential(azureApiCredentialDto, false);
    }

    public static ClientSecretCredential buildCredential(AzureApiCredentialDto azureApiCredentialDto, boolean disableSslVerify) {
        ClientSecretCredentialBuilder builder = new ClientSecretCredentialBuilder()
                .tenantId(azureApiCredentialDto.getTenantId())
                .clientId(azureApiCredentialDto.getClientId())
                .clientSecret(azureApiCredentialDto.getClientSecret());

        if (disableSslVerify) {
            builder.httpClient(createInsecureHttpClient());
        } else {
            log.info("Azure ClientSecretCredential configured with SSL verification ENABLED");
        }

        return builder.build();
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
