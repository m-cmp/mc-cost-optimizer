package com.mcmp.cost.azure.collector.batch.resource;

import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
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
public class AzureCostResourceItemProcessor implements ItemProcessor<AzureApiCredentialDto, List<AzureCostResourceDaily>> {

    private final AzureCostDailyService azureCostDailyService;

    @Override
    public List<AzureCostResourceDaily> process(AzureApiCredentialDto azureApiCredentialDto) throws Exception {
        log.info("Processing Azure resource(K8S/Object Storage) cost data for tenant: {}", azureApiCredentialDto.getTenantId());

        return azureCostDailyService.getCostByResources(azureApiCredentialDto);
    }
}
