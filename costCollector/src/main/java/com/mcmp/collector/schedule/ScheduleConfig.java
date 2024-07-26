package com.mcmp.collector.schedule;

import com.mcmp.collector.batch.UnusedCollect;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig {

    @Value("${unusedBatchCronSchedule}")
    public String batchCron ;

    @Bean
    public JobDetail UnusedJobDetail() {
        return JobBuilder.newJob().ofType(ScheduleJob.class)
                .storeDurably()
                .withIdentity("UnusedJob", "Unused")
                .withDescription("Execute Spring Batch Job with Quartz")
                .build();
    }

    @Bean
    public Trigger sampleJobTrigger(JobDetail UnusedJobDetail) {
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(batchCron);

        return TriggerBuilder.newTrigger().forJob(UnusedJobDetail)
                .withIdentity("UnusedJobTrigger1", "Unused")
                .withDescription("Unused Job Trigger")
                .withSchedule(cronSchedule)
                .build();
    }
}
