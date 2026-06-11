package com.mcmp.gcpcollector.batch.tasklet;

import com.mcmp.gcpcollector.dao.GcpBillingDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
@RequiredArgsConstructor
public class UpsertMonthlyTasklet implements Tasklet {

    private final GcpBillingDao gcpBillingDao;
    private final String date;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        String invoiceMonth = date.replace("-", "").substring(0, 6);
        gcpBillingDao.upsertMonthly(invoiceMonth);
        log.info("gcp_billing_monthly 갱신 완료 - 월: {}", invoiceMonth);
        return RepeatStatus.FINISHED;
    }
}
