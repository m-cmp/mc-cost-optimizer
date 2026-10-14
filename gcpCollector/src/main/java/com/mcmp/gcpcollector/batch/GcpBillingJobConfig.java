package com.mcmp.gcpcollector.batch;

import com.mcmp.gcpcollector.batch.processor.GcpBillingItemProcessor;
import com.mcmp.gcpcollector.batch.tasklet.AnomalyDetectTasklet;
import com.mcmp.gcpcollector.batch.tasklet.BudgetCheckTasklet;
import com.mcmp.gcpcollector.batch.tasklet.RightSizeTasklet;
import com.mcmp.gcpcollector.batch.tasklet.UnusedDetectTasklet;
import com.mcmp.gcpcollector.batch.tasklet.UpsertMonthlyTasklet;
import com.mcmp.gcpcollector.batch.writer.GcpBillingItemWriter;
import com.mcmp.gcpcollector.dao.GcpBillingDao;
import com.mcmp.gcpcollector.dto.GcpBillingRawDto;
import com.mcmp.gcpcollector.service.BillingQueryService;
import com.mcmp.gcpcollector.service.GcpAnomalyDetectionService;
import com.mcmp.gcpcollector.service.GcpBudgetCheckService;
import com.mcmp.gcpcollector.service.GcpRightSizeService;
import com.mcmp.gcpcollector.service.GcpUnusedDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GcpBillingJobConfig {

    private static final int CHUNK_SIZE = 500;

    private final BillingQueryService billingQueryService;
    private final GcpBillingDao gcpBillingDao;
    private final GcpAnomalyDetectionService gcpAnomalyDetectionService;
    private final GcpUnusedDetectionService gcpUnusedDetectionService;
    private final GcpBudgetCheckService gcpBudgetCheckService;
    private final GcpRightSizeService gcpRightSizeService;

    // ── Reader ────────────────────────────────────────────────────────────
    /**
     * date 를 마지막 날로 최근 lookbackDays 일(UTC)을 다시 조회한다. 하루치를 한 번만 가져오면 그 시점에 아직
     * 내보내지지 않은 마지막 몇 시간이 영영 빠진다(2026-10 실측: 매일 14~19시 UTC 이후 누락).
     * 조회에 성공해 행이 있을 때만 같은 기간의 기존 행을 지우고 다시 적재한다(조회 실패·빈 응답이면 기존 데이터 유지).
     */
    @Bean
    @StepScope
    public ListItemReader<Map<String, Object>> gcpBillingItemReader(
            @Value("#{jobParameters['date']}") String date,
            @Value("${gcpBillingLookbackDays:3}") int lookbackDays) {
        LocalDate lastDay = LocalDate.parse(date);
        LocalDate firstDay = lastDay.minusDays(Math.max(1, lookbackDays) - 1L);
        List<Map<String, Object>> rows = billingQueryService.getRawBillingByRange(firstDay, lastDay);
        log.info("BigQuery 조회 완료 - 기간: {} ~ {}, 건수: {}", firstDay, lastDay, rows.size());
        if (!rows.isEmpty()) {
            int deleted = gcpBillingDao.deleteByUsageRange(firstDay + " 00:00:00", lastDay.plusDays(1) + " 00:00:00");
            log.info("gcp_billing_raw 재적재 - 기존 {}건 삭제 후 {}건 적재 (기간: {} ~ {})", deleted, rows.size(), firstDay, lastDay);
        }
        return new ListItemReader<>(rows);
    }

    // ── Processor ─────────────────────────────────────────────────────────
    @Bean
    public GcpBillingItemProcessor gcpBillingItemProcessor() {
        return new GcpBillingItemProcessor();
    }

    // ── Writer ────────────────────────────────────────────────────────────
    @Bean
    public GcpBillingItemWriter gcpBillingItemWriter() {
        return new GcpBillingItemWriter(gcpBillingDao);
    }

    // ── Tasklets ──────────────────────────────────────────────────────────
    @Bean
    @StepScope
    public UpsertMonthlyTasklet upsertMonthlyTasklet(
            @Value("#{jobParameters['date']}") String date,
            @Value("${gcpBillingLookbackDays:3}") int lookbackDays) {
        // 재조회 기간이 전달에 걸치면(월초) 전달 합계도 다시 계산
        String firstDay = LocalDate.parse(date).minusDays(Math.max(1, lookbackDays) - 1L).toString();
        return new UpsertMonthlyTasklet(gcpBillingDao, firstDay, date);
    }

    @Bean
    public AnomalyDetectTasklet anomalyDetectTasklet() {
        return new AnomalyDetectTasklet(gcpAnomalyDetectionService);
    }

    @Bean
    public UnusedDetectTasklet unusedDetectTasklet() {
        return new UnusedDetectTasklet(gcpUnusedDetectionService);
    }

    @Bean
    public BudgetCheckTasklet budgetCheckTasklet() {
        return new BudgetCheckTasklet(gcpBudgetCheckService);
    }

    @Bean
    public RightSizeTasklet rightSizeTasklet() {
        return new RightSizeTasklet(gcpRightSizeService);
    }

    // ── Steps ─────────────────────────────────────────────────────────────
    @Bean
    public Step billingCollectStep(JobRepository jobRepository, PlatformTransactionManager tm) {
        return new StepBuilder("billingCollectStep", jobRepository)
                .<Map<String, Object>, GcpBillingRawDto>chunk(CHUNK_SIZE, tm)
                .reader(gcpBillingItemReader(null, 0))  // @StepScope: 실제 값은 jobParameters·설정에서 주입
                .processor(gcpBillingItemProcessor())
                .writer(gcpBillingItemWriter())
                .build();
    }

    @Bean
    public Step upsertMonthlyStep(JobRepository jobRepository, PlatformTransactionManager tm) {
        return new StepBuilder("upsertMonthlyStep", jobRepository)
                .tasklet(upsertMonthlyTasklet(null, 0), tm)
                .build();
    }

    @Bean
    public Step anomalyDetectStep(JobRepository jobRepository, PlatformTransactionManager tm) {
        return new StepBuilder("anomalyDetectStep", jobRepository)
                .tasklet(anomalyDetectTasklet(), tm)
                .build();
    }

    @Bean
    public Step unusedDetectStep(JobRepository jobRepository, PlatformTransactionManager tm) {
        return new StepBuilder("unusedDetectStep", jobRepository)
                .tasklet(unusedDetectTasklet(), tm)
                .build();
    }

    @Bean
    public Step budgetCheckStep(JobRepository jobRepository, PlatformTransactionManager tm) {
        return new StepBuilder("budgetCheckStep", jobRepository)
                .tasklet(budgetCheckTasklet(), tm)
                .build();
    }

    @Bean
    public Step rightSizeStep(JobRepository jobRepository, PlatformTransactionManager tm) {
        return new StepBuilder("rightSizeStep", jobRepository)
                .tasklet(rightSizeTasklet(), tm)
                .build();
    }

    // ── Job ───────────────────────────────────────────────────────────────
    @Bean
    public Job gcpBillingJob(JobRepository jobRepository,
                             Step billingCollectStep,
                             Step upsertMonthlyStep,
                             Step anomalyDetectStep,
                             Step unusedDetectStep,
                             Step budgetCheckStep,
                             Step rightSizeStep) {
        return new JobBuilder("gcpBillingJob", jobRepository)
                .start(billingCollectStep)
                .next(upsertMonthlyStep)
                .next(anomalyDetectStep)
                .next(unusedDetectStep)
                .next(budgetCheckStep)
                .next(rightSizeStep)
                .build();
    }
}
