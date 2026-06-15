package com.mcmp.cost.ncp.collector.batch.service;

import com.mcmp.cost.ncp.collector.batch.NcpBatchConstants;
import com.mcmp.cost.ncp.collector.batch.NcpCredentialItemReader;
import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
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
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class NcpCostServiceBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NcpCredentialItemReader ncpCredentialItemReader;
    private final NcpCostServiceItemProcessor ncpCostServiceItemProcessor;
    private final NcpCostServiceItemWriter ncpCostServiceItemWriter;

    @Bean(name = NcpBatchConstants.NCP_COST_SERVICE_JOB)
    public Job ncpCostServiceJob() {
        return new JobBuilder(NcpBatchConstants.NCP_COST_SERVICE_JOB, jobRepository)
                .start(ncpCostServiceStep())
                .build();
    }

    @Bean(name = NcpBatchConstants.NCP_COST_SERVICE_STEP)
    public Step ncpCostServiceStep() {
        return new StepBuilder(NcpBatchConstants.NCP_COST_SERVICE_STEP, jobRepository)
                .<NcpApiCredentialDto, List<NcpCostServiceMonth>>chunk(1, transactionManager)
                .reader(ncpCredentialItemReader)
                .processor(ncpCostServiceItemProcessor)
                .writer(ncpCostServiceItemWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
