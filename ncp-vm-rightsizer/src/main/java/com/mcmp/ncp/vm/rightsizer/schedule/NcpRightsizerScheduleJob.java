package com.mcmp.ncp.vm.rightsizer.schedule;

import com.mcmp.ncp.vm.rightsizer.batch.BatchExecutorService;
import com.mcmp.ncp.vm.rightsizer.batch.RightSizeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * NCP VM Rightsizer & Unused Resource Detection Batch 스케줄러
 * 매일 한국시간 09:30 (UTC 00:30)에 실행되어 VM rightsizing 분석 및 Unused 자원 탐지를 수행합니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NcpRightsizerScheduleJob extends QuartzJobBean {

    private final BatchExecutorService batchExecutorService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("====================================");
        log.info("Starting NCP VM Rightsizer Batch Quartz Job");
        log.info("Scheduled Time: Daily 09:30 KST (00:30 UTC)");
        log.info("====================================");

        try {
            // Unused Resource Detection 배치 실행 (우선순위 1 - unused_batch_rst에 기록)
            log.info("Executing Unused Resource Detection Batch...");
            batchExecutorService.executeBatch(RightSizeType.NCP_UNUSED_VM);

            // Anomaly VM 배치 실행
            log.info("Executing Anomaly VM Batch...");
            batchExecutorService.executeBatch(RightSizeType.NCP_ANOMALY_VM);

            // Size Down VM 배치 실행 (Recommend VM - unused_batch_rst 체크 후 중복 방지)
            log.info("Executing Size Down VM Batch (Recommend)...");
            batchExecutorService.executeBatch(RightSizeType.NCP_SIZE_DOWN_VM);

            log.info("NCP VM Rightsizer Batch Job completed successfully");
        } catch (Exception e) {
            log.error("Error executing NCP VM Rightsizer Batch Job", e);
            throw new JobExecutionException("Failed to execute NCP VM Rightsizer Batch Job", e);
        }

        log.info("====================================");
        log.info("NCP VM Rightsizer Batch Quartz Job finished");
        log.info("====================================");
    }
}
