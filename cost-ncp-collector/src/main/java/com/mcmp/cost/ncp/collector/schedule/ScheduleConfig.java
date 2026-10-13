package com.mcmp.cost.ncp.collector.schedule;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;


@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ncp-schedule")
@Data
public class ScheduleConfig {

    private String serviceBatchCron;
    private String vmBatchCron;
    private String resourceBatchCron;

    @PostConstruct
    public void init() {
        log.info("====================================");
        log.info("NCP Schedule Config Initialized");
        log.info("serviceBatchCron: {}", serviceBatchCron);
        log.info("vmBatchCron: {}", vmBatchCron);
        log.info("resourceBatchCron: {}", resourceBatchCron);
        log.info("====================================");
    }

    @Bean
    public JobDetail ncpServiceJobDetail() {
        return JobBuilder.newJob(NcpServiceScheduleJob.class)
                .withIdentity("NcpServiceJob", "NcpCost")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger ncpServiceJobTrigger(JobDetail ncpServiceJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(ncpServiceJobDetail)
                .withIdentity("NcpServiceJobTrigger", "NcpCost")
                .withSchedule(CronScheduleBuilder.cronSchedule(serviceBatchCron))
                .build();
    }

    @Bean
    public JobDetail ncpVmJobDetail() {
        return JobBuilder.newJob(NcpVmScheduleJob.class)
                .withIdentity("NcpVmJob", "NcpCost")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger ncpVmJobTrigger(JobDetail ncpVmJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(ncpVmJobDetail)
                .withIdentity("NcpVmJobTrigger", "NcpCost")
                .withSchedule(CronScheduleBuilder.cronSchedule(vmBatchCron))
                .build();
    }

    @Bean
    public JobDetail ncpResourceJobDetail() {
        return JobBuilder.newJob(NcpResourceScheduleJob.class)
                .withIdentity("NcpResourceJob", "NcpCost")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger ncpResourceJobTrigger(JobDetail ncpResourceJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(ncpResourceJobDetail)
                .withIdentity("NcpResourceJobTrigger", "NcpCost")
                .withSchedule(CronScheduleBuilder.cronSchedule(resourceBatchCron))
                .build();
    }
}
