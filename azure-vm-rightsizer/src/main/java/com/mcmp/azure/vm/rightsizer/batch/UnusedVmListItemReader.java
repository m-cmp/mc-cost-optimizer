package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.dto.UnusedVmDto;
import com.mcmp.azure.vm.rightsizer.mapper.AssetComputeMetricMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;

/**
 * 어제 날짜의 VM별 평균 CPU 사용률을 조회하는 ItemReader
 */
@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class UnusedVmListItemReader implements ItemReader<UnusedVmDto> {

    private final AssetComputeMetricMapper assetComputeMetricMapper;
    private Iterator<UnusedVmDto> unusedVmIterator;

    @Override
    public UnusedVmDto read() {
        // 어제 날짜의 VM별 평균 CPU 사용률 조회 (최초 1회만)
        if (unusedVmIterator == null) {
            List<UnusedVmDto> vmList = assetComputeMetricMapper.selectYesterdayAvgCpuByVm("AZURE");

            if (vmList == null || vmList.isEmpty()) {
                log.warn("No VMs found for yesterday's metric data (AZURE)");
                vmList = List.of();
            }

            unusedVmIterator = vmList.iterator();
            log.info("Azure VM metric data loaded: {} items (yesterday's average CPU usage)", vmList.size());
        }

        if (unusedVmIterator.hasNext()) {
            UnusedVmDto unusedVmDto = unusedVmIterator.next();
            log.debug("Reading VM metric: {}, avg CPU: {}%",
                unusedVmDto.getVmId(), unusedVmDto.getYesterdayAvgCpu());
            return unusedVmDto;
        }

        return null;
    }
}
