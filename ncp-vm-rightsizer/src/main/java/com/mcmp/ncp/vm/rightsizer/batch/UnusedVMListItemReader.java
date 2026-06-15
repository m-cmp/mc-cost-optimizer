package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.dto.UnusedDto;
import com.mcmp.ncp.vm.rightsizer.mapper.AssetComputeMetricMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;

/**
 * 어제 날짜의 Instance별 평균 CPU 사용률을 조회하는 ItemReader
 */
@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class UnusedVMListItemReader implements ItemReader<UnusedDto> {

    private final AssetComputeMetricMapper assetComputeMetricMapper;
    private Iterator<UnusedDto> unusedIterator;

    @Override
    public UnusedDto read() {
        // 어제 날짜의 Instance별 평균 CPU 사용률 조회 (최초 1회만)
        if (unusedIterator == null) {
            List<UnusedDto> instanceList = assetComputeMetricMapper.selectYesterdayAvgCpuByInstance("NCP");

            if (instanceList == null || instanceList.isEmpty()) {
                log.warn("No Instances found for yesterday's metric data (NCP)");
                instanceList = List.of();
            }

            unusedIterator = instanceList.iterator();
            log.info("NCP Instance metric data loaded: {} items (yesterday's average CPU usage)", instanceList.size());
        }

        if (unusedIterator.hasNext()) {
            UnusedDto unusedDto = unusedIterator.next();
            log.debug("Reading Instance metric: {}, avg CPU: {}%",
                unusedDto.getInstanceNo(), unusedDto.getYesterdayAvgCpu());
            return unusedDto;
        }

        return null;
    }
}
