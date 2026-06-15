package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.azure.vm.rightsizer.service.AnomalyCostVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AnomalyVmListItemProcessor implements ItemProcessor<AzureCostVmDailyDto, AnomalyDto> {

    private final AnomalyCostVmService anomalyCostVmService;

    @Override
    public AnomalyDto process(AzureCostVmDailyDto azureCostVmDailyDto) throws Exception {
        return anomalyCostVmService.getAnomalyCostByVmId(azureCostVmDailyDto);
    }
}
