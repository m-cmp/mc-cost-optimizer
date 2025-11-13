package com.mcmp.ncp.vm.rightsizer.schedule;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;


@Slf4j
@Configuration
@ConfigurationProperties(prefix = "ncp-vm-rightsizer-schedule")
@Data
public class ScheduleConfig {

    private String rightsizerBatchCron;

    @PostConstruct
    public void init() {
        log.info("====================================");
        log.info("NCP VM Rightsizer Schedule Config Initialized");
        log.info("rightsizerBatchCron: {}", rightsizerBatchCron);
        log.info("====================================");
    }

    @Bean
    public JobDetail ncpRightsizerJobDetail() {
        return JobBuilder.newJob(NcpRightsizerScheduleJob.class)
                .withIdentity("NcpRightsizerJob", "NcpRightsizer")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger ncpRightsizerJobTrigger(JobDetail ncpRightsizerJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(ncpRightsizerJobDetail)
                .withIdentity("NcpRightsizerJobTrigger", "NcpRightsizer")
                .withSchedule(CronScheduleBuilder.cronSchedule(rightsizerBatchCron))
                .build();
    }
}
