package com.mcmp.collector.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class ScheduleJob extends QuartzJobBean {
    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("unusedCollectJob")
    @Autowired
    private Job unusedCollectJob;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting Quartz Job");

        try {
            ZonedDateTime now = ZonedDateTime.now();
            String endOfToday = String.valueOf(now.toLocalDate().atStartOfDay());
            String startOfToday = String.valueOf(now.toLocalDate().minusDays(1).atStartOfDay());

            JobParameters jobParameter = new JobParametersBuilder()
                    .addString("startDt", startOfToday)
                    .addString("endDt", endOfToday)
                    .addLong("createTime", System.currentTimeMillis())
                    .toJobParameters();


            jobLauncher.run(unusedCollectJob, jobParameter);
        } catch (Exception e) {
            log.error("Error executing Spring Batch Job", e);
            throw new JobExecutionException("Failed to execute Spring Batch Job", e);
        }

        log.info("Quartz Job completed");
    }
}
