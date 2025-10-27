package com.mcmp.cost.azure.collector.batch.vm;

import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.service.AzureCostDailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AzureCostVmItemProcessor implements ItemProcessor<AzureApiCredentialDto, List<AzureCostVmDaily>> {

    private final AzureCostDailyService azureCostDailyService;

    @Override
    public List<AzureCostVmDaily> process(AzureApiCredentialDto azureApiCredentialDto) throws Exception {
        log.info("Processing Azure cost data for tenant: {}", azureApiCredentialDto.getTenantId());

        return azureCostDailyService.getCostByVirtualMachines(azureApiCredentialDto);
    }
}
