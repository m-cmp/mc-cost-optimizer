package com.mcmp.collector.schedule;

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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
public class CurScheduleJob extends QuartzJobBean {

    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("curCollectJob")
    @Autowired
    private Job curCollectJob;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting CUR Quartz Job");

        try {
            LocalDateTime now = LocalDateTime.now();

            String seq;
            if(now.getHour() == 0){
                seq = "1";
            } else if (now.getHour() == 6){
                seq = "2";
            } else {
                seq = "0";
            }

            JobParameters jobParameter = new JobParametersBuilder()
                    .addString("batchType", "curCollect")
                    .addString("seq", seq)
                    .addLong("createTime", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(curCollectJob, jobParameter);
        } catch (Exception e) {
            log.error("Error executing Cur Batch Job", e);
            throw new JobExecutionException("Failed to execute Cur Batch Job", e);
        }

        log.info("Quartz Job (Cur Batch) completed");
    }
}
