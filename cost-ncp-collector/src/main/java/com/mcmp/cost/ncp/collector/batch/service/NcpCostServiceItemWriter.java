package com.mcmp.cost.ncp.collector.batch.service;

import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
import com.mcmp.cost.ncp.collector.repository.NcpCostServiceMonthRepository;
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
public class NcpCostServiceItemWriter implements ItemWriter<List<NcpCostServiceMonth>> {

    private final NcpCostServiceMonthRepository ncpCostServiceMonthRepository;

    @Override
    public void write(Chunk<? extends List<NcpCostServiceMonth>> chunk) throws Exception {
        for (List<NcpCostServiceMonth> ncpCostServiceMonthList : chunk) {
            if (ncpCostServiceMonthList != null && !ncpCostServiceMonthList.isEmpty()) {
                ncpCostServiceMonthRepository.saveAll(ncpCostServiceMonthList);
                log.info("Saved {} Ncp cost service records to database", ncpCostServiceMonthList.size());
            }
        }
    }
}
