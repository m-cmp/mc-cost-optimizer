package com.mcmp.cost.azure.collector.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class BatchExecutorService {

    private final JobLauncher jobLauncher;
    private final Map<String, Job> jobMap;

    public void executeBatch(AzureBatchType azureBatchType) {
        Job job = getJobByType(azureBatchType);
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis(), true)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.info("{} Batch Job started with execution id: {}",
                    azureBatchType.getDisplayName(), jobExecution.getId());

        } catch (Exception e) {
            log.error("Error running {} Batch Job", azureBatchType.getDisplayName(), e);
            throw new RuntimeException("Batch execution failed: " + azureBatchType.getDisplayName(), e);
        }
    }

    private Job getJobByType(AzureBatchType azureBatchType) {
        Job job = jobMap.get(azureBatchType.getJobBeanName());
        if (job == null) {
            throw new IllegalArgumentException("Job not found for type: " + azureBatchType);
        }
        return job;
    }
}
