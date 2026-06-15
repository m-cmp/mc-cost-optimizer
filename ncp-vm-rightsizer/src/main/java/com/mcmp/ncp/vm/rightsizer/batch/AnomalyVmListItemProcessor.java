package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import com.mcmp.ncp.vm.rightsizer.service.AnomalyCostVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AnomalyVmListItemProcessor implements ItemProcessor<NcpCostVmMonthDto, AnomalyDto> {

    private final AnomalyCostVmService anomalyCostVmService;

    @Override
    public AnomalyDto process(NcpCostVmMonthDto ncpCostVmMonthDto) throws Exception {
        return anomalyCostVmService.getAnomalyCostByVmId(ncpCostVmMonthDto);
    }
}
