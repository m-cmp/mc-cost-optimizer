package com.mcmp.cost.ncp.collector.batch.resource;

import com.mcmp.cost.ncp.collector.entity.NcpCostResourceMonth;
import com.mcmp.cost.ncp.collector.mapper.NcpCostResourceDailyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class NcpCostResourceItemWriter implements ItemWriter<List<NcpCostResourceMonth>> {

    private final NcpCostResourceDailyMapper ncpCostResourceDailyMapper;

    @Override
    public void write(Chunk<? extends List<NcpCostResourceMonth>> chunk) throws Exception {
        // 월 누적 → 일별 차분 적재 (월 누적 행 자체는 서비스에서 INSERT IGNORE 로 이미 적재됨)
        for (List<NcpCostResourceMonth> list : chunk) {
            if (list == null || list.isEmpty()) continue;
            int totalInserted = 0;
            for (NcpCostResourceMonth row : list) {
                totalInserted += ncpCostResourceDailyMapper.insertDailyCost(row, row.getSnapshotDate());
            }
            log.info("Saved {} Ncp resource daily records to database", totalInserted);
        }
    }
}
