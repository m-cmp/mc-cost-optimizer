package com.mcmp.cost.azure.collector.batch.resource;

import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
import com.mcmp.cost.azure.collector.repository.AzureCostResourceDailyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AzureCostResourceItemWriter implements ItemWriter<List<AzureCostResourceDaily>> {

    private final AzureCostResourceDailyRepository azureCostResourceDailyRepository;

    @Override
    public void write(Chunk<? extends List<AzureCostResourceDaily>> chunk) throws Exception {
        for (List<AzureCostResourceDaily> list : chunk) {
            if (list == null || list.isEmpty()) continue;

            // 재수집 멱등: 같은 subscription·usage_date 를 지우고 다시 넣는다 (chunk 트랜잭션 안이라 원자적)
            String subscriptionId = list.get(0).getSubscriptionId();
            Set<String> usageDates = list.stream().map(AzureCostResourceDaily::getUsageDate).collect(Collectors.toSet());
            int deleted = azureCostResourceDailyRepository.deleteBySubscriptionIdAndUsageDates(subscriptionId, usageDates);

            azureCostResourceDailyRepository.saveAll(list);
            azureCostResourceDailyRepository.flush();
            log.info("Saved {} Azure cost resource records to database (replaced {} rows for dates {})", list.size(), deleted, usageDates);
        }
    }
}
