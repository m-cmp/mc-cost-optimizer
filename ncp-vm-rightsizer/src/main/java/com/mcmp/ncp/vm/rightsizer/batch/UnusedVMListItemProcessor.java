package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.dto.UnusedProcessMartDto;
import com.mcmp.ncp.vm.rightsizer.dto.UnusedDto;
import com.mcmp.ncp.vm.rightsizer.mapper.UnusedProcessMartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * Unused Instance 판정 Processor
 * 1. unused_daily_mart에 어제 평균 CPU 저장
 * 2. 14일간 데이터 분석하여 unused 판정
 */
@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class UnusedVMListItemProcessor implements ItemProcessor<UnusedDto, UnusedDto> {

    private final UnusedProcessMartMapper unusedProcessMartMapper;

    @Override
    public UnusedDto process(UnusedDto item) throws Exception {
        // 1. unused_daily_mart에 어제 평균 CPU 저장
        UnusedProcessMartDto martDto = UnusedProcessMartDto.builder()
            .createDt(LocalDateTime.now())
            .resourceId(item.getInstanceNo())
            .collectDt(item.getYesterdayDate())
            .metricType("cpu")
            .metricAvgAmount(item.getYesterdayAvgCpu())
            .build();

        unusedProcessMartMapper.insertUnusedProcessMart(martDto);
        log.debug("Saved to unused_daily_mart: Instance={}, Date={}, AvgCPU={}%",
            item.getInstanceNo(), item.getYesterdayDate(), item.getYesterdayAvgCpu());

        // 2. 14일간 데이터 분석
        UnusedDto stats = unusedProcessMartMapper.select14DaysMetricStats(item.getInstanceNo());

        if (stats == null) {
            log.debug("Not enough 14-day data for Instance: {}", item.getInstanceNo());
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
            log.debug("Instance {} is NOT unused (avg={}, max={})",
                item.getInstanceNo(), stats.getAvgCpu14Days(), stats.getMaxCpu14Days());
            return null;  // Unused 아니면 알림 X
        }

        // Unused 등급 설정
        item.setUnusedRating("Unused");
        log.info("Instance {} detected as UNUSED (avg={}, max={}, days={})",
            item.getInstanceNo(), stats.getAvgCpu14Days(), stats.getMaxCpu14Days(), stats.getDayCount());

        return item;
    }
}


