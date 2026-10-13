package com.mcmp.cost.ncp.collector.batch.resource;

import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostResourceMonth;
import com.mcmp.cost.ncp.collector.service.NcpCostResourceService;
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
public class NcpCostResourceItemProcessor implements ItemProcessor<NcpApiCredentialDto, List<NcpCostResourceMonth>> {

    private final NcpCostResourceService ncpCostResourceService;

    @Override
    public List<NcpCostResourceMonth> process(NcpApiCredentialDto ncpApiCredentialDto) throws Exception {
        log.info("Processing Ncp resource(K8S/Object Storage) cost data");
        return ncpCostResourceService.getCostByResource(ncpApiCredentialDto);
    }
}
