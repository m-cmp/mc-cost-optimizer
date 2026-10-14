package com.mcmp.cost.azure.collector.service;

import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import java.util.List;

public interface AzureCostDailyService {

    /**
     * 하루 전날의 Azure Service 별 요금을 조회한다. </br>
     * <p>
     * 조회 하는 API의 공식 문서는 다음과 같다.
     * <a href="https://learn.microsoft.com/ko-kr/rest/api/cost-management/query/usage?view=rest-cost-management-2023-11-01&tabs=HTTP">Query - Usage</a>
     *
     * @param azureApiCredentialDto {@link AzureApiCredentialDto}
     * @return {@link AzureCostServiceDaily}
     */
    List<AzureCostServiceDaily> getCostByService(AzureApiCredentialDto azureApiCredentialDto);

    /**
     * 하루 전날의 Azure Virtual Machines 별 요금을 조회한다. </br>
     * <p>
     * 조회 하는 API의 공식 문서는 다음과 같다.
     * <a href="https://learn.microsoft.com/ko-kr/rest/api/cost-management/query/usage?view=rest-cost-management-2023-11-01&tabs=HTTP">Query - Usage</a>
     *
     * @param azureApiCredentialDto {@link AzureApiCredentialDto}
     * @return {@link AzureCostVmDaily}
     */
    List<AzureCostVmDaily> getCostByVirtualMachines(AzureApiCredentialDto azureApiCredentialDto);

    /**
     * 하루 전날의 Azure 비-VM 자원(AKS managed cluster, Storage Account) 별 요금을 조회한다. </br>
     * <p>
     * VM 경로와 달리 ARM 리소스 조회(getById) 를 하지 않으며, 조회 실패 시 예외 대신 빈 목록을 반환한다.
     * 같은 Query - Usage API 를 ResourceType 필터로 호출한다.
     *
     * @param azureApiCredentialDto {@link AzureApiCredentialDto}
     * @return {@link AzureCostResourceDaily}
     */
    List<AzureCostResourceDaily> getCostByResources(AzureApiCredentialDto azureApiCredentialDto);
}
