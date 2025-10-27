package com.mcmp.cost.ncp.collector.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class BatchExecutorService {

    private final JobLauncher jobLauncher;
    private final Map<String, Job> jobMap;

    public void executeBatch(NcpBatchType ncpBatchType) {
        Job job = getJobByType(ncpBatchType);
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis(), true)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.info("{} Batch Job started with execution id: {}",
                    ncpBatchType.getDisplayName(), jobExecution.getId());

        } catch (Exception e) {
            log.error("Error running {} Batch Job", ncpBatchType.getDisplayName(), e);
            throw new RuntimeException("Batch execution failed: " + ncpBatchType.getDisplayName(), e);
        }
    }

    private Job getJobByType(NcpBatchType ncpBatchType) {
        Job job = jobMap.get(ncpBatchType.getJobBeanName());
        if (job == null) {
            throw new IllegalArgumentException("Job not found for type: " + ncpBatchType);
        }
        return job;
    }
}
