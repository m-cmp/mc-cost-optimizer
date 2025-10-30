package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.ncp.vm.rightsizer.service.RecommendVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class RecommendVmListItemProcessor implements ItemProcessor<NcpCostVmMonthDto, RecommendVmTypeDto> {

    private final RecommendVmService recommendVmService;

    @Override
    public RecommendVmTypeDto process(NcpCostVmMonthDto ncpCostVmMonthDto) throws Exception {
        log.info("Processing NCP Vm Recommend data.");
        return recommendVmService.getRecommendSizeDownVm(ncpCostVmMonthDto.getMemberNo(), ncpCostVmMonthDto.getInstanceNo());
    }
}
