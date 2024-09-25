package com.processor.costprocessor.schedule;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScheduleConfig {

    @Value("${unusedProcessCronSchedule}")
    public String batchCron;

    @Value("${abnormalProcessCronSchedule}")
    public String abnormalCron;

    @Bean
    public JobDetail UnusedProcessJobDetail() {
        return JobBuilder.newJob().ofType(ScheduleJob.class)
                .storeDurably()
                .withIdentity("UnusedProcessJob", "Unused")
                .withDescription("Execute Spring Batch Job with Quartz")
                .build();
    }

    @Bean
    public Trigger JobTrigger(JobDetail UnusedProcessJobDetail) {
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(batchCron);

        return TriggerBuilder.newTrigger().forJob(UnusedProcessJobDetail)
                .withIdentity("UnusedProcessJobTrigger1", "Unused")
                .withDescription("Unused Job Trigger")
                .withSchedule(cronSchedule)
                .build();
    }

    @Bean
    public JobDetail AbnormalProcessJobDetail() {
        return JobBuilder.newJob().ofType(AbnormalJob.class)
                .storeDurably()
                .withIdentity("AbnoramlProcessJob", "Abnormal")
                .withDescription("Execute Spring Batch Job with Quartz")
                .build();
    }

    @Bean
    public Trigger AbnormalJobTrigger(JobDetail AbnormalProcessJobDetail) {
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(abnormalCron);

        return TriggerBuilder.newTrigger().forJob(AbnormalProcessJobDetail)
                .withIdentity("AbnormalProcessJobTrigger1", "Abnormal")
                .withDescription("Abnormal Job Trigger")
                .withSchedule(cronSchedule)
                .build();
    }
}
