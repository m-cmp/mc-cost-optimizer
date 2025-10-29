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
public class RecommendVmListItemReader implements ItemReader<AzureCostVmDailyDto> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AzureCostVmDailyMapper azureCostVmDailyMapper;
    private Iterator<AzureCostVmDailyDto> vmDailyDtoIterator;

    @Override
    public AzureCostVmDailyDto read() {
        if (vmDailyDtoIterator == null) {
            // TODO : for Test 추후 정확한 요건에 따라 조회 방식을 변경해야한다.
            String vmId = "vm-capshp-prd-krc-web01";
            AzureCostVmDailyDto sizeUpTagetVm = azureCostVmDailyMapper
                    .findLatestBySubscriptionIdAndVmId(azureCredentialProperties.getSubscriptionId(), vmId);
            List<AzureCostVmDailyDto> azureCostVmDailyDtoList = List.of(sizeUpTagetVm);
            vmDailyDtoIterator = azureCostVmDailyDtoList.iterator();
            log.info("Azure Vm loaded: {} items", azureCostVmDailyDtoList.size());
        }

        if (vmDailyDtoIterator.hasNext()) {
            AzureCostVmDailyDto azureCostVmDailyDto = vmDailyDtoIterator.next();
            log.debug("Reading Vm for subscription.");
            return azureCostVmDailyDto;
        }

        return null;
    }
}
