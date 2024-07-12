package com.mcmp.optiunusedbatch.config;

import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail sampleJobDetail() {
        return JobBuilder.newJob().ofType(QuartzJob.class)
                .storeDurably()
                .withIdentity("SampleJob", "test")
                .withDescription("Execute Spring Batch Job with Quartz")
                .build();
    }

    @Bean
    public Trigger sampleJobTrigger(JobDetail jobDetail) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(5)
                .repeatForever();

        return TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity("SampleJobTrigger")
                .withDescription("Sample Job Trigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory) throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();
        if (!scheduler.checkExists(job.getKey())) {
            scheduler.scheduleJob(job, trigger);
        } else {
            scheduler.rescheduleJob(trigger.getKey(), trigger);
        }
        scheduler.start();
        return scheduler;
    }


}
