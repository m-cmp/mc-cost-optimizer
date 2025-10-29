package com.mcmp.azure.vm.rightsizer.service;

import com.mcmp.azure.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;

public interface AnomalyCostVmService {

    /**
     * 일일 VM을 통해 이상비용을 탐지한다.
     *
     * @param azureCostVmDailyDto VM 일일 비용.
     * @return 이상 비용 DTO.
     */
    AnomalyDto getAnomalyCostByVmId(AzureCostVmDailyDto azureCostVmDailyDto);

}
