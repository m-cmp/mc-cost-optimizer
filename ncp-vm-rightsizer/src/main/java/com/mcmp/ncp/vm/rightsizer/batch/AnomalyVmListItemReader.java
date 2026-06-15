package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import com.mcmp.ncp.vm.rightsizer.mapper.NcpCostVmMonthMapper;
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
public class AnomalyVmListItemReader implements ItemReader<NcpCostVmMonthDto> {

    private final NcpCostVmMonthMapper ncpCostVmMonthMapper;
    private Iterator<NcpCostVmMonthDto> vmDailyDtoIterator;

    @Override
    public NcpCostVmMonthDto read() {
        // 가잔 최신(이번달)으로 수집된 VM List를 조회한다.
        if (vmDailyDtoIterator == null) {
            List<NcpCostVmMonthDto> vmLists = ncpCostVmMonthMapper.findVmListCurrentMonth();
            vmDailyDtoIterator = vmLists.iterator();
            log.info("NCP Vm loaded: {} items", vmLists.size());
        }

        if (vmDailyDtoIterator.hasNext()) {
            NcpCostVmMonthDto ncpCostVmMonthDto = vmDailyDtoIterator.next();
            log.debug("Reading vm.");
            return ncpCostVmMonthDto;
        }

        return null;
    }
}
