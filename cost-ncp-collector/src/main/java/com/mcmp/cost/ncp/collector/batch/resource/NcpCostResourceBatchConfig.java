package com.mcmp.cost.ncp.collector.batch.resource;

import com.mcmp.cost.ncp.collector.batch.NcpBatchConstants;
import com.mcmp.cost.ncp.collector.batch.NcpCredentialItemReader;
import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostResourceMonth;
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

/**
 * NCP 비-VM 자원(NKS, Object Storage) 비용 수집 Job. VM Job(ncpCostVmJob) 과 분리하고 예산 체크 step 은 붙이지 않는다.
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class NcpCostResourceBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NcpCredentialItemReader ncpCredentialItemReader;
    private final NcpCostResourceItemProcessor ncpCostResourceItemProcessor;
    private final NcpCostResourceItemWriter ncpCostResourceItemWriter;

    @Bean(name = NcpBatchConstants.NCP_COST_RESOURCE_JOB)
    public Job ncpCostResourceJob() {
        return new JobBuilder(NcpBatchConstants.NCP_COST_RESOURCE_JOB, jobRepository)
                .start(ncpCostResourceStep())
                .build();
    }

    @Bean(name = NcpBatchConstants.NCP_COST_RESOURCE_STEP)
    public Step ncpCostResourceStep() {
        return new StepBuilder(NcpBatchConstants.NCP_COST_RESOURCE_STEP, jobRepository)
                .<NcpApiCredentialDto, List<NcpCostResourceMonth>>chunk(1, transactionManager)
                .reader(ncpCredentialItemReader)
                .processor(ncpCostResourceItemProcessor)
                .writer(ncpCostResourceItemWriter)
                .allowStartIfComplete(true)
                .build();
    }
}
