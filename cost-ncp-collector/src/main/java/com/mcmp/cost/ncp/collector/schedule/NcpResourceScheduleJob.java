package com.mcmp.cost.ncp.collector.schedule;

import com.mcmp.cost.ncp.collector.batch.BatchExecutorService;
import com.mcmp.cost.ncp.collector.batch.NcpBatchType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * NCP 비-VM 자원(NKS, Object Storage) Cost Batch 스케줄러
 * 기본 매일 한국시간 09:45 (UTC 00:45) — VM 배치(00:30 UTC) 뒤에 실행한다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NcpResourceScheduleJob extends QuartzJobBean {

    private final BatchExecutorService batchExecutorService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("====================================");
        log.info("Starting NCP Resource(K8S/Object Storage) Cost Batch Quartz Job");
        log.info("Scheduled Time: Daily 09:45 KST (00:45 UTC)");
        log.info("====================================");

        try {
            batchExecutorService.executeBatch(NcpBatchType.NCP_COST_RESOURCE);
            log.info("NCP Resource Cost Batch Job completed successfully");
        } catch (Exception e) {
            log.error("Error executing NCP Resource Cost Batch Job", e);
            throw new JobExecutionException("Failed to execute NCP Resource Cost Batch Job", e);
        }

        log.info("====================================");
        log.info("NCP Resource Cost Batch Quartz Job finished");
        log.info("====================================");
    }
}
