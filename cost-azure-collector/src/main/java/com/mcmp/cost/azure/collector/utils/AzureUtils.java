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
import com.azure.resourcemanager.costmanagement.models.QueryColumn;
import com.azure.resourcemanager.costmanagement.models.QueryColumnType;
import com.azure.resourcemanager.costmanagement.models.QueryComparisonExpression;
import com.azure.resourcemanager.costmanagement.models.QueryDataset;
import com.azure.resourcemanager.costmanagement.models.QueryDefinition;
import com.azure.resourcemanager.costmanagement.models.QueryFilter;
import com.azure.resourcemanager.costmanagement.models.QueryGrouping;
import com.azure.resourcemanager.costmanagement.models.QueryOperatorType;
import com.azure.resourcemanager.costmanagement.models.QueryResult;
import com.azure.resourcemanager.costmanagement.models.QueryTimePeriod;
import com.azure.resourcemanager.costmanagement.models.TimeframeType;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
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

    /**
     * 최근 lookbackDays일(어제까지, UTC 기준) 비-VM 자원(ResourceId) 단위 일별 비용.
     * Cost Management 데이터는 수 시간~하루 늦게 채워지므로, 하루치만 조회하면 덜 채워진 날이 그대로 굳는다.
     * 매 실행마다 최근 며칠을 다시 조회해 덮어쓰면(writer 가 usage_date 단위로 delete 후 insert) 늦게 들어온 비용과
     * 이전 실행이 실패한 날짜도 다음 실행에서 채워진다.
     * 대상: AKS managed cluster, AKS 노드 VM 스케일셋, Storage account — ResourceType dimension 필터({@link AzureResourceType#armTypes()}).
     * grouping 은 기존 VM 쿼리와 같은 3개(ResourceGroupName, ResourceId, ResourceLocation) 로 고정한다(문서상 2개 제한, 3개 운영 확인).
     * 응답 row: [PreTaxCost, UsageDate, ResourceGroupName, ResourceId, ResourceLocation, Currency] — 단 인덱스에 의존하지 말고 {@link #columnIndex} 사용.
     */
    public static QueryDefinition getQueryCostByResources(int lookbackDays) {
        // 컨테이너 TZ(Asia/Seoul)와 무관하게 UTC 날짜 기준으로 [어제-(lookbackDays-1), 어제]
        LocalDate lastDay = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        LocalDate firstDay = lastDay.minusDays(Math.max(1, lookbackDays) - 1L);
        OffsetDateTime startOfDay = firstDay.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endOfDay = lastDay.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

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
                                        .withName("ResourceLocation")
                        ))
                        .withFilter(new QueryFilter()
                                .withDimensions(
                                        new QueryComparisonExpression()
                                                .withName("ResourceType")
                                                .withOperator(QueryOperatorType.IN)
                                                .withValues(AzureResourceType.armTypes())
                                ))
                );
    }

    /** 응답 컬럼명 → 인덱스. 고정 인덱스 대신 이름으로 해석해 grouping 순서 변경에 견디게 한다. */
    public static Map<String, Integer> columnIndex(QueryResult queryResult) {
        return columnIndex(queryResult.columns());
    }

    /** 컬럼 목록 → 인덱스. 다음 페이지(QueryResultInner)처럼 QueryResult 가 아닌 응답에도 쓰기 위한 오버로드. */
    public static Map<String, Integer> columnIndex(List<QueryColumn> cols) {
        Map<String, Integer> idx = new HashMap<>();
        if (cols == null) return idx;
        for (int i = 0; i < cols.size(); i++) {
            idx.put(cols.get(i).name(), i);
        }
        return idx;
    }
}
