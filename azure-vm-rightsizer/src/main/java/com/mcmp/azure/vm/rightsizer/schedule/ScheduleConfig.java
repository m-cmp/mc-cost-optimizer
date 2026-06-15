package com.mcmp.azure.vm.rightsizer.schedule;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;


@Slf4j
@Configuration
@ConfigurationProperties(prefix = "azure-vm-rightsizer-schedule")
@Data
public class ScheduleConfig {

    private String rightsizerBatchCron;

    @PostConstruct
    public void init() {
        log.info("====================================");
        log.info("Azure VM Rightsizer Schedule Config Initialized");
        log.info("rightsizerBatchCron: {}", rightsizerBatchCron);
        log.info("====================================");
    }

    @Bean
    public JobDetail azureRightsizerJobDetail() {
        return JobBuilder.newJob(AzureRightsizerScheduleJob.class)
                .withIdentity("AzureRightsizerJob", "AzureRightsizer")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger azureRightsizerJobTrigger(JobDetail azureRightsizerJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(azureRightsizerJobDetail)
                .withIdentity("AzureRightsizerJobTrigger", "AzureRightsizer")
                .withSchedule(CronScheduleBuilder.cronSchedule(rightsizerBatchCron))
                .build();
    }
}
