package com.mcmp.cost.ncp.collector.batch.vm;

import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import com.mcmp.cost.ncp.collector.mapper.NcpCostVmDailyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class NcpCostVmItemWriter implements ItemWriter<List<NcpCostVmMonth>> {

    private final NcpCostVmDailyMapper ncpCostVmDailyMapper;

    @Override
    public void write(Chunk<? extends List<NcpCostVmMonth>> chunk) throws Exception {
        // 당월 VM 비용 저장
        for (List<NcpCostVmMonth> ncpCostVmMonthList : chunk) {
            if (ncpCostVmMonthList != null && !ncpCostVmMonthList.isEmpty()) {
                int totalInserted = 0;
                for (NcpCostVmMonth ncpCostVmMonth : ncpCostVmMonthList) {
                    // writeDate를 LocalDate로 변환 (날짜 부분만 추출)
                    LocalDate targetDate = ncpCostVmMonth.getWriteDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // 일일 데이터 적재
                    int inserted = ncpCostVmDailyMapper.insertDailyCost(
                            ncpCostVmMonth.getInstanceNo(),
                            targetDate
                    );
                    totalInserted += inserted;
                }
                log.info("Saved {} Ncp cost daily records to database", totalInserted);
            }
        }
    }
}
