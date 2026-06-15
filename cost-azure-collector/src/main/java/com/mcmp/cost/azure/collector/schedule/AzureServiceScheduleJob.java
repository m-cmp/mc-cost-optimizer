package com.mcmp.cost.azure.collector.schedule;

import com.mcmp.cost.azure.collector.batch.BatchExecutorService;
import com.mcmp.cost.azure.collector.batch.AzureBatchType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * AZURE Service Cost Batch 스케줄러
 * 매일 한국시간 09:00 (UTC 00:00)에 실행되어 전날 NCP Service 비용 데이터를 수집합니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AzureServiceScheduleJob extends QuartzJobBean {

    private final BatchExecutorService batchExecutorService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("====================================");
        log.info("Starting AZURE Service Cost Batch Quartz Job");
        log.info("Scheduled Time: Daily 09:00 KST (00:00 UTC)");
        log.info("====================================");

        try {
            batchExecutorService.executeBatch(AzureBatchType.AZURE_COST_SERVICE);
            log.info("AZURE Service Cost Batch Job completed successfully");
        } catch (Exception e) {
            log.error("Error executing AZURE Service Cost Batch Job", e);
            throw new JobExecutionException("Failed to execute AZURE Service Cost Batch Job", e);
        }

        log.info("====================================");
        log.info("AZURE Service Cost Batch Quartz Job finished");
        log.info("====================================");
    }
}
