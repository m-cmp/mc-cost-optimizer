package com.mcmp.cost.azure.collector.batch.service;

import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
import com.mcmp.cost.azure.collector.repository.AzureCostServiceDailyRepository;
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
public class AzureCostServiceItemWriter implements ItemWriter<List<AzureCostServiceDaily>> {

    private final AzureCostServiceDailyRepository azureCostServiceDailyRepository;

    @Override
    public void write(Chunk<? extends List<AzureCostServiceDaily>> chunk) throws Exception {
        for (List<AzureCostServiceDaily> azureCostServiceDailyList : chunk) {
            if (azureCostServiceDailyList != null && !azureCostServiceDailyList.isEmpty()) {
                azureCostServiceDailyRepository.saveAll(azureCostServiceDailyList);
                log.info("Saved {} Azure cost service records to database", azureCostServiceDailyList.size());
            }
        }
    }
}
