package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.dto.UnusedProcessMartDto;
import com.mcmp.azure.vm.rightsizer.dto.UnusedVmDto;
import com.mcmp.azure.vm.rightsizer.mapper.UnusedProcessMartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Unused VM 판정 Processor
 * 1. unused_process_mart에 어제 평균 CPU 저장
 * 2. 14일간 데이터 분석하여 unused 판정
 */
@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class UnusedVmMetricProcessor implements ItemProcessor<UnusedVmDto, UnusedVmDto> {

    private final UnusedProcessMartMapper unusedProcessMartMapper;

    @Override
    public UnusedVmDto process(UnusedVmDto item) throws Exception {
        // 1. unused_daily_mart에 어제 평균 CPU 저장
        UnusedProcessMartDto martDto = UnusedProcessMartDto.builder()
            .createDt(LocalDateTime.now())
            .resourceId(item.getVmId())
            .collectDt(item.getYesterdayDate())
            .metricType("cpu")
            .metricAvgAmount(item.getYesterdayAvgCpu())
            .build();

        unusedProcessMartMapper.insertUnusedProcessMart(martDto);
        log.debug("Saved to unused_daily_mart: VM={}, Date={}, AvgCPU={}%",
            item.getVmId(), item.getYesterdayDate(), item.getYesterdayAvgCpu());

        // 2. 14일간 데이터 분석
        UnusedVmDto stats = unusedProcessMartMapper.select14DaysMetricStats(item.getVmId());

        if (stats == null) {
            log.debug("Not enough 14-day data for VM: {}", item.getVmId());
            return null;  // 14일 데이터 부족 시 알림 X
        }

        // 원본 DTO에 14일 통계 추가
        item.setAvgCpu14Days(stats.getAvgCpu14Days());
        item.setMaxCpu14Days(stats.getMaxCpu14Days());
        item.setDayCount(stats.getDayCount());

        // 3. Unused 판정 (14일 평균 < 1 OR 14일 최대 < 3)
        boolean isUnused = (stats.getAvgCpu14Days() != null && stats.getAvgCpu14Days() < 1.0)
                        || (stats.getMaxCpu14Days() != null && stats.getMaxCpu14Days() < 3.0);

        if (!isUnused) {
            log.debug("VM {} is NOT unused (avg={}, max={})",
                item.getVmId(), stats.getAvgCpu14Days(), stats.getMaxCpu14Days());
            return null;  // Unused 아니면 알림 X
        }

        // Unused 등급 설정
        item.setUnusedRating("Unused");
        log.info("VM {} detected as UNUSED (avg={}, max={}, days={})",
            item.getVmId(), stats.getAvgCpu14Days(), stats.getMaxCpu14Days(), stats.getDayCount());

        return item;
    }
}
