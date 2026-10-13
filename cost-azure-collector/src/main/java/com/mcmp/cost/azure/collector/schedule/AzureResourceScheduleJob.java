package com.mcmp.cost.azure.collector.schedule;

import com.mcmp.cost.azure.collector.batch.AzureBatchType;
import com.mcmp.cost.azure.collector.batch.BatchExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * Azure 비-VM 자원(AKS, Storage Account) Cost Batch 스케줄러
 * 실행 시각은 azure-schedule.resource-batch-cron (컨테이너 TZ 기준). VM 배치 뒤에 실행해 Cost Management 스로틀링을 분산한다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AzureResourceScheduleJob extends QuartzJobBean {

    private final BatchExecutorService batchExecutorService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("====================================");
        log.info("Starting AZURE Resource(K8S/Object Storage) Cost Batch Quartz Job");
        log.info("Scheduled by azure-schedule.resource-batch-cron");
        log.info("====================================");

        try {
            batchExecutorService.executeBatch(AzureBatchType.AZURE_COST_RESOURCE);
            log.info("AZURE Resource Cost Batch Job completed successfully");
        } catch (Exception e) {
            log.error("Error executing AZURE Resource Cost Batch Job", e);
            throw new JobExecutionException("Failed to execute AZURE Resource Cost Batch Job", e);
        }

        log.info("====================================");
        log.info("AZURE Resource Cost Batch Quartz Job finished");
        log.info("====================================");
    }
}
