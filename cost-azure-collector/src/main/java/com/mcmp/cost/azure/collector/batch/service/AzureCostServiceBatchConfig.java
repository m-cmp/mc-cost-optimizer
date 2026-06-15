package com.mcmp.cost.azure.collector.batch.service;

import com.mcmp.cost.azure.collector.batch.AzureBatchConstants;
import com.mcmp.cost.azure.collector.batch.AzureCredentialItemReader;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostServiceDaily;
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
public class AzureCostServiceBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AzureCredentialItemReader azureCredentialItemReader;
    private final AzureCostServiceItemProcessor azureCostServiceItemProcessor;
    private final AzureCostServiceItemWriter azureCostServiceItemWriter;

    @Bean(name = AzureBatchConstants.AZURE_COST_SERVICE_JOB)
    public Job azureCostServiceJob() {
        return new JobBuilder(AzureBatchConstants.AZURE_COST_SERVICE_JOB, jobRepository)
                .start(azureCostServiceStep())
                .build();
    }

    @Bean(name = AzureBatchConstants.AZURE_COST_SERVICE_STEP)
    public Step azureCostServiceStep() {
        return new StepBuilder(AzureBatchConstants.AZURE_COST_SERVICE_STEP, jobRepository)
                .<AzureApiCredentialDto, List<AzureCostServiceDaily>>chunk(1, transactionManager)
                .reader(azureCredentialItemReader)
                .processor(azureCostServiceItemProcessor)
                .writer(azureCostServiceItemWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
