package com.mcmp.cost.ncp.collector.batch.vm;

import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import com.mcmp.cost.ncp.collector.service.NcpCostMonthService;
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
public class NcpCostVmItemProcessor implements ItemProcessor<NcpApiCredentialDto, List<NcpCostVmMonth>> {

    private final NcpCostMonthService ncpCostMonthService;

    @Override
    public List<NcpCostVmMonth> process(NcpApiCredentialDto ncpApiCredentialDto) throws Exception {
        log.info("Processing Ncp VM cost data for tenant: {}", ncpApiCredentialDto);
        return ncpCostMonthService.getCostByVm(ncpApiCredentialDto);
    }
}
