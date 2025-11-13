package com.mcmp.cost.azure.collector.batch.vm;

import com.mcmp.cost.azure.collector.batch.AzureBatchConstants;
import com.mcmp.cost.azure.collector.batch.AzureCredentialItemReader;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class AzureCostVmBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AzureCredentialItemReader azureCredentialItemReader;
    private final AzureCostVmItemProcessor azureCostVmItemProcessor;
    private final AzureCostVmItemWriter azureCostVmItemWriter;
    private final BudgetCheckTasklet budgetCheckTasklet;

    @Bean(name = AzureBatchConstants.AZURE_COST_VM_JOB)
    public Job azureCostVmJob() {
        return new JobBuilder(AzureBatchConstants.AZURE_COST_VM_JOB, jobRepository)
                .start(azureCostVmStep())
                .next(budgetCheckStep())
                .build();
    }

    @Bean(name = AzureBatchConstants.AZURE_COST_VM_STEP)
    public Step azureCostVmStep() {
        return new StepBuilder(AzureBatchConstants.AZURE_COST_VM_STEP, jobRepository)
                .<AzureApiCredentialDto, List<AzureCostVmDaily>>chunk(1, transactionManager)
                .reader(azureCredentialItemReader)
                .processor(azureCostVmItemProcessor)
                .writer(azureCostVmItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean(name = "azureBudgetCheckStep")
    public Step budgetCheckStep() {
        return new StepBuilder("azureBudgetCheckStep", jobRepository)
                .tasklet(budgetCheckTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
