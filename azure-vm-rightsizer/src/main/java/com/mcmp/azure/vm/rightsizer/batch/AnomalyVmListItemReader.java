package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import com.mcmp.azure.vm.rightsizer.mapper.AzureCostVmDailyMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AnomalyVmListItemReader implements ItemReader<AzureCostVmDailyDto> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AzureCostVmDailyMapper azureCostVmDailyMapper;
    private Iterator<AzureCostVmDailyDto> vmDailyDtoIterator;

    @Override
    public AzureCostVmDailyDto read() {
        // 가잔 최신으로 수집된 VM List를 조회한다.
        if (vmDailyDtoIterator == null) {
            List<AzureCostVmDailyDto> vmLists = azureCostVmDailyMapper
                    .findLatestBySubscriptionId(azureCredentialProperties.getSubscriptionId());
            vmDailyDtoIterator = vmLists.iterator();
            log.info("Azure Vm loaded: {} items", vmLists.size());
        }

        if (vmDailyDtoIterator.hasNext()) {
            AzureCostVmDailyDto azureApiCredentialDto = vmDailyDtoIterator.next();
            log.debug("Reading credential for tenant.");
            return azureApiCredentialDto;
        }

        return null;
    }
}
