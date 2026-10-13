package com.mcmp.cost.azure.collector.service.impl;

import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.costmanagement.CostManagementManager;
import com.azure.resourcemanager.costmanagement.models.QueryDefinition;
import com.azure.resourcemanager.costmanagement.models.QueryResult;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.properties.AzureSslProperties;
import com.mcmp.cost.azure.collector.service.AzureCostDailyService;
import com.mcmp.cost.azure.collector.utils.AzureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AzureCostDailyServiceImpl implements AzureCostDailyService {

    private final AzureSslProperties azureSslProperties;

    /** 비-VM 자원 비용을 매 실행마다 다시 조회할 기간(일). Cost Management 데이터 지연·실패한 날 재수집용 */
    @Value("${azure-schedule.resource-lookback-days:3}")
    private int resourceLookbackDays;

    @Override
    public List<AzureCostServiceDaily> getCostByService(AzureApiCredentialDto azureApiCredentialDto) {
        // 0. 인증 생성
        ClientSecretCredential credential = AzureUtils.buildCredential(azureApiCredentialDto, azureSslProperties.isDisabled());

        // 1. Profile 생성
        AzureProfile profile = AzureUtils.buildProfile(azureApiCredentialDto);

        // 2. CostManagementManager 생성
        CostManagementManager costManager = CostManagementManager.authenticate(credential, profile);

        // 3. QueryDefinition 작성
        QueryDefinition query = AzureUtils.getQueryCostByServieName();

        // 4. scope 정의
        String scope = "/subscriptions/" + azureApiCredentialDto.getSubscriptionId();

        // 5. API 호출
        QueryResult queryResult = costManager
                .queries()
                .usage(scope, query);

        // 6. DB Insert
        List<AzureCostServiceDaily> azureCostServiceDailyList = new ArrayList<>();
        for (List<Object> row : queryResult.rows()) {
            AzureCostServiceDaily azureCostServiceDaily = AzureCostServiceDaily.builder()
                    .tenantId(azureApiCredentialDto.getTenantId())
                    .subscriptionId(azureApiCredentialDto.getSubscriptionId())
                    .preTaxCost((double) row.get(0))
                    .usageDate(row.get(1).toString())
                    .serviceName(row.get(2).toString())
                    .currency(row.get(3).toString())
                    .build();
            azureCostServiceDailyList.add(azureCostServiceDaily);
            log.debug("azureCostServiceDaily data: {}", azureCostServiceDaily.toString());
        }
        return azureCostServiceDailyList;
    }

    @Override
    public List<AzureCostVmDaily> getCostByVirtualMachines(AzureApiCredentialDto azureApiCredentialDto) {
        // 0. 인증 생성
        ClientSecretCredential credential = AzureUtils.buildCredential(azureApiCredentialDto, azureSslProperties.isDisabled());

        // 1. Profile 생성
        AzureProfile profile = AzureUtils.buildProfile(azureApiCredentialDto);

        // 2. CostManagementManager 생성
        CostManagementManager costManager = CostManagementManager.authenticate(credential, profile);

        // 3. QueryDefinition 작성
        QueryDefinition query = AzureUtils.getQueryCostByVirtualMachines();

        // 4. scope 정의
        String scope = "/subscriptions/" + azureApiCredentialDto.getSubscriptionId();

        // 5. API 호출
        QueryResult queryResult = costManager
                .queries()
                .usage(scope, query);

        AzureResourceManager azureResourceManager = AzureResourceManager.authenticate(credential, profile)
                .withSubscription(azureApiCredentialDto.getSubscriptionId());

        // 6. DB Insert
        List<AzureCostVmDaily> azureCostVmDailyList = new ArrayList<>();
        for (List<Object> row : queryResult.rows()) {
            String resourceId = row.get(3).toString();

            try {
                // 7. VM 정보 조회.
                VirtualMachine vm = azureResourceManager.virtualMachines().getById(resourceId);

                AzureCostVmDaily azureCostVmDaily = AzureCostVmDaily.builder()
                        .tenantId(azureApiCredentialDto.getTenantId())
                        .subscriptionId(azureApiCredentialDto.getSubscriptionId())
                        .preTaxCost((double) row.get(0))
                        .usageDate(row.get(1).toString())
                        .resourceGroupName(row.get(2).toString())
                        .resourceId(resourceId)
                        .region(vm.regionName())
                        .instanceType(vm.size().getValue())
                        .osType(vm.osType().name())
                        .vmId(vm.name())
                        .resourceGuid(row.get(4).toString())
                        .currency(row.get(5).toString())
                        .build();
                azureCostVmDailyList.add(azureCostVmDaily);
                log.debug("azureCostVmDaily data: {}", azureCostVmDaily.toString());
            } catch (Exception e) {
                log.warn("VM not found (possibly deleted): {}, skipping...", resourceId);
            }
        }
        return azureCostVmDailyList;
    }

    @Override
    public List<AzureCostResourceDaily> getCostByResources(AzureApiCredentialDto azureApiCredentialDto) {
        // 0. 인증 생성
        ClientSecretCredential credential = AzureUtils.buildCredential(azureApiCredentialDto, azureSslProperties.isDisabled());

        // 1. Profile 생성
        AzureProfile profile = AzureUtils.buildProfile(azureApiCredentialDto);

        // 2. CostManagementManager 생성
        CostManagementManager costManager = CostManagementManager.authenticate(credential, profile);

        // 3. QueryDefinition 작성 (ResourceType IN (managedclusters, virtualmachinescalesets, storageaccounts), 최근 resourceLookbackDays일)
        QueryDefinition query = AzureUtils.getQueryCostByResources(resourceLookbackDays);
        log.info("Azure resource cost query window: last {} day(s) up to yesterday (UTC), subscription={}",
                resourceLookbackDays, azureApiCredentialDto.getSubscriptionId());

        // 4. scope 정의
        String scope = "/subscriptions/" + azureApiCredentialDto.getSubscriptionId();

        // 5. API 호출 — 실패(429/5xx/인증) 시 빈 목록으로 끝내 step 은 성공시킨다. 아무것도 지우지 않으며,
        //    이번에 못 가져온 날짜는 다음 실행의 조회 기간(resourceLookbackDays)에 포함되어 다시 수집된다
        QueryResult queryResult;
        try {
            queryResult = costManager
                    .queries()
                    .usage(scope, query);
        } catch (Exception e) {
            log.error("Azure resource(K8S/Object Storage) cost query failed. subscription={}, reason={}",
                    azureApiCredentialDto.getSubscriptionId(), e.getMessage(), e);
            return new ArrayList<>();
        }
        // 6. 다음 페이지가 있으면 끝까지 가져온다. 하나라도 실패하면 이번 결과 전체를 버린다:
        //    writer 는 결과에 있는 날짜를 통째로 지우고 다시 넣으므로, 일부 페이지만 저장하면 나머지 자원의 그 날짜 행이 사라진다.
        List<List<Object>> rows = new ArrayList<>();
        if (queryResult.rows() != null) rows.addAll(queryResult.rows());
        if (queryResult.nextLink() != null && !queryResult.nextLink().isBlank()) {
            try {
                int pages = AzureCostQueryPager.fetchRemainingRows(
                        costManager.serviceClient().getHttpPipeline(), query, queryResult.nextLink(), rows);
                log.info("Azure resource cost query was paged: fetched {} additional page(s). subscription={}",
                        pages, azureApiCredentialDto.getSubscriptionId());
            } catch (Exception e) {
                log.error("Azure resource cost query paging failed; discarding this run's result (will be re-collected next run). subscription={}, reason={}",
                        azureApiCredentialDto.getSubscriptionId(), e.getMessage(), e);
                return new ArrayList<>();
            }
        }

        // 7. 매핑 (VM 경로와 달리 getById 조회 없음). 응답 형식이 예상과 달라 매핑 자체가 실패하면 기존 데이터를 지우지 않도록 빈 목록
        List<AzureCostResourceDaily> list;
        try {
            list = AzureCostResourceRowMapper.toEntities(queryResult.columns(), rows, azureApiCredentialDto);
        } catch (Exception e) {
            log.error("Azure resource cost mapping failed; discarding this run's result. subscription={}, columns={}, reason={}",
                    azureApiCredentialDto.getSubscriptionId(), queryResult.columns(), e.getMessage(), e);
            return new ArrayList<>();
        }
        log.info("Azure resource cost rows: fetched={}, mapped={}, subscription={}",
                rows.size(), list.size(), azureApiCredentialDto.getSubscriptionId());
        for (AzureCostResourceDaily row : list) {
            log.debug("azureCostResourceDaily data: {}", row);
        }
        return list;
    }
}
