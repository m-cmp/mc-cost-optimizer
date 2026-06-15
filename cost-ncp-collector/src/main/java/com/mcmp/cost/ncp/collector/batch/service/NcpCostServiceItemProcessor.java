package com.mcmp.cost.ncp.collector.batch.service;

import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
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
public class NcpCostServiceItemProcessor implements ItemProcessor<NcpApiCredentialDto, List<NcpCostServiceMonth>> {

    private final NcpCostMonthService ncpCostMonthService;

    @Override
    public List<NcpCostServiceMonth> process(NcpApiCredentialDto ncpApiCredentialDto) throws Exception {
        log.info("Processing Ncp Service cost data for tenant.");
        return ncpCostMonthService.getCostByService(ncpApiCredentialDto);
    }
}
