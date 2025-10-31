package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class RightSizeBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RecommendVmListItemReader recommendVmListItemReader;
    private final RecommendVmListItemProcessor recommendVmItemProcessor;
    private final RecommendVmListItemWriter recommendVmListItemWriter;
    private final AnomalyVmListItemReader anomalyVmListItemReader;
    private final AnomalyVmListItemProcessor anomalyVmItemProcessor;
    private final AnomalyVmListItemWriter anomalyVmItemWriter;

    @Bean(name = RightSizeBatchConstants.SIZE_DOWN_JOB)
    public Job recommendVmJob() {
        return new JobBuilder(RightSizeBatchConstants.SIZE_DOWN_JOB, jobRepository)
                .start(recommendVmStep())
                .build();
    }

    @Bean(name = RightSizeBatchConstants.SIZE_DOWN_STEP)
    public Step recommendVmStep() {
        return new StepBuilder(RightSizeBatchConstants.SIZE_DOWN_STEP, jobRepository)
                .<NcpCostVmMonthDto, RecommendVmTypeDto>chunk(1, transactionManager)
                .reader(recommendVmListItemReader)
                .processor(recommendVmItemProcessor)
                .writer(recommendVmListItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean(name = RightSizeBatchConstants.ANOMALY_JOB)
    public Job anomalyVmJob() {
        return new JobBuilder(RightSizeBatchConstants.ANOMALY_JOB, jobRepository)
                .start(anomalyVmStep())
                .build();
    }

    @Bean(name = RightSizeBatchConstants.ANOMALY_STEP)
    public Step anomalyVmStep() {
        return new StepBuilder(RightSizeBatchConstants.ANOMALY_STEP, jobRepository)
                .<NcpCostVmMonthDto, AnomalyDto>chunk(1, transactionManager)
                .reader(anomalyVmListItemReader)
                .processor(anomalyVmItemProcessor)
                .writer(anomalyVmItemWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
