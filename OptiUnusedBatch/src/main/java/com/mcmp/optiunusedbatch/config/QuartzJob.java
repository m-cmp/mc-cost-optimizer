package com.mcmp.optiunusedbatch.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QuartzJob extends QuartzJobBean {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job sampleSpringBatchJob;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting Quartz Job");

        try {
            // Spring Batch Job 실행
            jobLauncher.run(sampleSpringBatchJob, new JobParametersBuilder().addLong("startAt", System.currentTimeMillis()).toJobParameters());
        } catch (Exception e) {
            log.error("Error executing Spring Batch Job", e);
            throw new JobExecutionException("Failed to execute Spring Batch Job", e);
        }

        log.info("Quartz Job completed");
    }
}
