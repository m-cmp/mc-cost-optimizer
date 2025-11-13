package com.mcmp.cost.azure.collector.service.impl;

import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.costmanagement.CostManagementManager;
import com.azure.resourcemanager.costmanagement.models.QueryDefinition;
import com.azure.resourcemanager.costmanagement.models.QueryResult;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.properties.AzureSslProperties;
import com.mcmp.cost.azure.collector.service.AzureCostDailyService;
import com.mcmp.cost.azure.collector.utils.AzureUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AzureCostDailyServiceImpl implements AzureCostDailyService {

    private final AzureSslProperties azureSslProperties;

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
        }
        return azureCostVmDailyList;
    }
}
