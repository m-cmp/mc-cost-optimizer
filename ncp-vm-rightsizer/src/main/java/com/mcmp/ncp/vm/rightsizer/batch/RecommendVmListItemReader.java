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
public class RecommendVmListItemReader implements ItemReader<NcpCostVmMonthDto> {

    private final NcpCostVmMonthMapper ncpCostVmMonthMapper;
    private Iterator<NcpCostVmMonthDto> vmDailyDtoIterator;

    @Override
    public NcpCostVmMonthDto read() {
        if (vmDailyDtoIterator == null) {
            // TODO : for Test 추후 정확한 요건에 따라 조회 방식을 변경해야한다.
            String memberNo = "3059708";
            String instanceNo = "16880170";
            NcpCostVmMonthDto sizeDownTaget = ncpCostVmMonthMapper
                    .findLatestByMemberNoAndInstanceNo(memberNo, instanceNo);
            List<NcpCostVmMonthDto> ncpCostVmMonthDtoList = List.of(sizeDownTaget);
            vmDailyDtoIterator = ncpCostVmMonthDtoList.iterator();
            log.info("NCP Vm loaded: {} items", ncpCostVmMonthDtoList.size());
        }

        if (vmDailyDtoIterator.hasNext()) {
            NcpCostVmMonthDto ncpCostVmMonthDto = vmDailyDtoIterator.next();
            log.debug("Reading Vm for memberNo.");
            return ncpCostVmMonthDto;
        }

        return null;
    }
}
