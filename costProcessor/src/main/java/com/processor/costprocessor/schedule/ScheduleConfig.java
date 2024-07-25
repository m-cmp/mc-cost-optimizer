package com.processor.costprocessor.schedule;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig {

    @Value("${unusedProcessCronSchedule}")
    public String batchCron;

    @Bean
    public JobDetail UnusedProcessJobDetail() {
        return JobBuilder.newJob().ofType(ScheduleJob.class)
                .storeDurably()
                .withIdentity("UnusedProcessJob", "Unused")
                .withDescription("Execute Spring Batch Job with Quartz")
                .build();
    }

    @Bean
    public Trigger JobTrigger(JobDetail UnusedJobDetail) {
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(batchCron);

        return TriggerBuilder.newTrigger().forJob(UnusedJobDetail)
                .withIdentity("UnusedProcessJobTrigger1", "Unused")
                .withDescription("Unused Job Trigger")
                .withSchedule(cronSchedule)
                .build();
    }
}
