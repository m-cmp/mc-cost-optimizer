package com.processor.costprocessor.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduleJob extends QuartzJobBean {

    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("unusedProcessJob")
    @Autowired
    private Job unusedProcessJob;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting Quartz Job");

        try {

            JobParameters jobParameter = new JobParametersBuilder()
                    .addLong("createTime", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(unusedProcessJob, jobParameter);
        } catch (Exception e) {
            log.error("Error executing Spring Batch Job", e);
            throw new JobExecutionException("Failed to execute Spring Batch Job", e);
        }

        log.info("Quartz Job completed");
    }
}
