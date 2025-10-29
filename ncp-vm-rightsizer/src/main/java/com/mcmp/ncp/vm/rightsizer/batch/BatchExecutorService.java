package com.mcmp.ncp.vm.rightsizer.batch;

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

    public void executeBatch(RightSizeType rightSizeType) {
        Job job = getJobByType(rightSizeType);
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis(), true)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.info("{} Batch Job started with execution id: {}",
                    rightSizeType.getDisplayName(), jobExecution.getId());

        } catch (Exception e) {
            log.error("Error running {} Batch Job", rightSizeType.getDisplayName(), e);
            throw new RuntimeException("Batch execution failed: " + rightSizeType.getDisplayName(), e);
        }
    }

    private Job getJobByType(RightSizeType rightSizeType) {
        Job job = jobMap.get(rightSizeType.getJobBeanName());
        if (job == null) {
            throw new IllegalArgumentException("Job not found for type: " + rightSizeType);
        }
        return job;
    }
}
