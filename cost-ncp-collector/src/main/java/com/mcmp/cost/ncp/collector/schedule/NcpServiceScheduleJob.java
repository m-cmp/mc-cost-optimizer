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
 * NCP Service Cost Batch 스케줄러
 * 매일 한국시간 09:00 (UTC 00:00)에 실행되어 전날 NCP Service 비용 데이터를 수집합니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NcpServiceScheduleJob extends QuartzJobBean {

    private final BatchExecutorService batchExecutorService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("====================================");
        log.info("Starting NCP Service Cost Batch Quartz Job");
        log.info("Scheduled Time: Daily 09:00 KST (00:00 UTC)");
        log.info("====================================");

        try {
            batchExecutorService.executeBatch(NcpBatchType.NCP_COST_SERVICE);
            log.info("NCP Service Cost Batch Job completed successfully");
        } catch (Exception e) {
            log.error("Error executing NCP Service Cost Batch Job", e);
            throw new JobExecutionException("Failed to execute NCP Service Cost Batch Job", e);
        }

        log.info("====================================");
        log.info("NCP Service Cost Batch Quartz Job finished");
        log.info("====================================");
    }
}
