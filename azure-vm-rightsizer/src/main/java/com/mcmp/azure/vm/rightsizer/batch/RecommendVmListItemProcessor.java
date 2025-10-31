package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.azure.vm.rightsizer.service.RecommendVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class RecommendVmListItemProcessor implements ItemProcessor<AzureCostVmDailyDto, RecommendVmTypeDto> {

    private final RecommendVmService recommendVmService;

    @Override
    public RecommendVmTypeDto process(AzureCostVmDailyDto azureCostVmDailyDto) throws Exception {
        log.info("Processing Azure Vm Recommend data.");
        return recommendVmService.getRecommendSizeUpVm(azureCostVmDailyDto.getVmId());
    }
}
