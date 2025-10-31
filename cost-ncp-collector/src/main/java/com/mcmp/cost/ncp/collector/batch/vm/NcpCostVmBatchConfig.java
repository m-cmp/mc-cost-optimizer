package com.mcmp.cost.ncp.collector.batch.vm;

import com.mcmp.cost.ncp.collector.batch.NcpBatchConstants;
import com.mcmp.cost.ncp.collector.batch.NcpCredentialItemReader;
import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
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
public class NcpCostVmBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NcpCredentialItemReader ncpCredentialItemReader;
    private final NcpCostVmItemProcessor ncpCostVmItemProcessor;
    private final NcpCostVmItemWriter ncpCostVmItemWriter;

    @Bean(name = NcpBatchConstants.NCP_COST_VM_JOB)
    public Job ncpCostVmJob() {
        return new JobBuilder(NcpBatchConstants.NCP_COST_VM_JOB, jobRepository)
                .start(ncpCostVmStep())
                .build();
    }

    @Bean(name = NcpBatchConstants.NCP_COST_VM_STEP)
    public Step ncpCostVmStep() {
        return new StepBuilder(NcpBatchConstants.NCP_COST_VM_STEP, jobRepository)
                .<NcpApiCredentialDto, List<NcpCostVmMonth>>chunk(1, transactionManager)
                .reader(ncpCredentialItemReader)
                .processor(ncpCostVmItemProcessor)
                .writer(ncpCostVmItemWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
