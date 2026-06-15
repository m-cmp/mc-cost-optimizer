package com.mcmp.cost.azure.collector.batch.vm;

import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.repository.AzureCostVmDailyRepository;
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
public class AzureCostVmItemWriter implements ItemWriter<List<AzureCostVmDaily>> {

    private final AzureCostVmDailyRepository azureCostVmDailyRepository;

    @Override
    public void write(Chunk<? extends List<AzureCostVmDaily>> chunk) throws Exception {
        // 당일 VM 비용 저장
        for (List<AzureCostVmDaily> azureCostVmDailyList : chunk) {
            if (azureCostVmDailyList != null && !azureCostVmDailyList.isEmpty()) {
                azureCostVmDailyRepository.saveAll(azureCostVmDailyList);
                azureCostVmDailyRepository.flush();
                log.info("Saved {} Azure cost vm records to database", azureCostVmDailyList.size());
            }
        }
    }
}
