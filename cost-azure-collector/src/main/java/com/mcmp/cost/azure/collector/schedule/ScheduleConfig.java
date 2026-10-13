package com.mcmp.cost.azure.collector.schedule;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
@ConfigurationProperties(prefix = "azure-schedule")
@Data
public class ScheduleConfig {

    private String serviceBatchCron;
    private String vmBatchCron;
    private String resourceBatchCron;

    @PostConstruct
    public void init() {
        log.info("====================================");
        log.info("AZURE Schedule Config Initialized");
        log.info("serviceBatchCron: {}", serviceBatchCron);
        log.info("vmBatchCron: {}", vmBatchCron);
        log.info("resourceBatchCron: {}", resourceBatchCron);
        log.info("====================================");
    }

    @Bean
    public JobDetail azureServiceJobDetail() {
        return JobBuilder.newJob(AzureServiceScheduleJob.class)
                .withIdentity("AzureServiceJob", "AzureCost")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger azureServiceJobTrigger(JobDetail azureServiceJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(azureServiceJobDetail)
                .withIdentity("AzureServiceJobTrigger", "AzureCost")
                .withSchedule(CronScheduleBuilder.cronSchedule(serviceBatchCron))
                .build();
    }

    @Bean
    public JobDetail azureVmJobDetail() {
        return JobBuilder.newJob(AzureVmScheduleJob.class)
                .withIdentity("AzureVmJob", "AzureCost")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger azureVmJobTrigger(JobDetail azureVmJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(azureVmJobDetail)
                .withIdentity("AzureVmJobTrigger", "AzureCost")
                .withSchedule(CronScheduleBuilder.cronSchedule(vmBatchCron))
                .build();
    }

    @Bean
    public JobDetail azureResourceJobDetail() {
        return JobBuilder.newJob(AzureResourceScheduleJob.class)
                .withIdentity("AzureResourceJob", "AzureCost")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger azureResourceJobTrigger(JobDetail azureResourceJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(azureResourceJobDetail)
                .withIdentity("AzureResourceJobTrigger", "AzureCost")
                .withSchedule(CronScheduleBuilder.cronSchedule(resourceBatchCron))
                .build();
    }
}
