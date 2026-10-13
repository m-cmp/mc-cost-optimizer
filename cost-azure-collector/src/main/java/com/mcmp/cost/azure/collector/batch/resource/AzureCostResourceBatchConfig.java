package com.mcmp.cost.azure.collector.batch.resource;

import com.mcmp.cost.azure.collector.batch.AzureBatchConstants;
import com.mcmp.cost.azure.collector.batch.AzureCredentialItemReader;
import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.entity.AzureCostResourceDaily;
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

/**
 * Azure 비-VM 자원(AKS, Storage Account) 비용 수집 Job. VM Job(azureCostVmJob) 과 분리해 실패를 격리하고,
 * 예산 체크 step 은 붙이지 않는다(예산 로직은 범위 밖).
 */
@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class AzureCostResourceBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AzureCredentialItemReader azureCredentialItemReader;
    private final AzureCostResourceItemProcessor azureCostResourceItemProcessor;
    private final AzureCostResourceItemWriter azureCostResourceItemWriter;

    @Bean(name = AzureBatchConstants.AZURE_COST_RESOURCE_JOB)
    public Job azureCostResourceJob() {
        return new JobBuilder(AzureBatchConstants.AZURE_COST_RESOURCE_JOB, jobRepository)
                .start(azureCostResourceStep())
                .build();
    }

    @Bean(name = AzureBatchConstants.AZURE_COST_RESOURCE_STEP)
    public Step azureCostResourceStep() {
        return new StepBuilder(AzureBatchConstants.AZURE_COST_RESOURCE_STEP, jobRepository)
                .<AzureApiCredentialDto, List<AzureCostResourceDaily>>chunk(1, transactionManager)
                .reader(azureCredentialItemReader)
                .processor(azureCostResourceItemProcessor)
                .writer(azureCostResourceItemWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
