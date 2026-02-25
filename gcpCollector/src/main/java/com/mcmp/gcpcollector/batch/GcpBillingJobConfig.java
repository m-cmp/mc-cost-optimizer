package com.mcmp.gcpcollector.batch;

import com.mcmp.gcpcollector.batch.processor.GcpBillingItemProcessor;
import com.mcmp.gcpcollector.batch.tasklet.AnomalyDetectTasklet;
import com.mcmp.gcpcollector.batch.tasklet.BudgetCheckTasklet;
import com.mcmp.gcpcollector.batch.tasklet.UnusedDetectTasklet;
import com.mcmp.gcpcollector.batch.tasklet.UpsertMonthlyTasklet;
import com.mcmp.gcpcollector.batch.writer.GcpBillingItemWriter;
import com.mcmp.gcpcollector.dao.GcpBillingDao;
import com.mcmp.gcpcollector.dto.GcpBillingRawDto;
import com.mcmp.gcpcollector.service.BillingQueryService;
import com.mcmp.gcpcollector.service.GcpAnomalyDetectionService;
import com.mcmp.gcpcollector.service.GcpBudgetCheckService;
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

    // ── Reader ────────────────────────────────────────────────────────────
    @Bean
    @StepScope
    public ListItemReader<Map<String, Object>> gcpBillingItemReader(
            @Value("#{jobParameters['date']}") String date) {
        List<Map<String, Object>> rows = billingQueryService.getRawBillingByDate(date);
        log.info("BigQuery 조회 완료 - date: {}, 건수: {}", date, rows.size());
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
            @Value("#{jobParameters['date']}") String date) {
        return new UpsertMonthlyTasklet(gcpBillingDao, date);
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

    // ── Steps ─────────────────────────────────────────────────────────────
    @Bean
    public Step billingCollectStep(JobRepository jobRepository, PlatformTransactionManager tm) {
        return new StepBuilder("billingCollectStep", jobRepository)
                .<Map<String, Object>, GcpBillingRawDto>chunk(CHUNK_SIZE, tm)
                .reader(gcpBillingItemReader(null))
                .processor(gcpBillingItemProcessor())
                .writer(gcpBillingItemWriter())
                .build();
    }

    @Bean
    public Step upsertMonthlyStep(JobRepository jobRepository, PlatformTransactionManager tm) {
        return new StepBuilder("upsertMonthlyStep", jobRepository)
                .tasklet(upsertMonthlyTasklet(null), tm)
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

    // ── Job ───────────────────────────────────────────────────────────────
    @Bean
    public Job gcpBillingJob(JobRepository jobRepository,
                             Step billingCollectStep,
                             Step upsertMonthlyStep,
                             Step anomalyDetectStep,
                             Step unusedDetectStep,
                             Step budgetCheckStep) {
        return new JobBuilder("gcpBillingJob", jobRepository)
                .start(billingCollectStep)
                .next(upsertMonthlyStep)
                .next(anomalyDetectStep)
                .next(unusedDetectStep)
                .next(budgetCheckStep)
                .build();
    }
}
