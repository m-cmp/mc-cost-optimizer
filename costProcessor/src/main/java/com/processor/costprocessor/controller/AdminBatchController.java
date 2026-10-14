package com.processor.costprocessor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 배치 주기(Quartz cron)를 기다리지 않고 즉시 실행하기 위한 관리용 엔드포인트.
 * unusedProcessJob 은 Unused 판정 로직 안에서 Resize(자원추천)까지 같이 처리하므로
 * 별도의 recommend 트리거는 없음.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/costopti/costprs/admin")
public class AdminBatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Qualifier("unusedProcessJob")
    @Autowired
    private Job unusedProcessJob;

    @Qualifier("abnormalProcessJob")
    @Autowired
    private Job abnormalProcessJob;

    @GetMapping("/unused/run")
    public ResponseEntity<String> runUnused() {
        return runJob(unusedProcessJob, "unusedProcessJob");
    }

    @GetMapping("/abnormal/run")
    public ResponseEntity<String> runAbnormal() {
        return runJob(abnormalProcessJob, "abnormalProcessJob");
    }

    private ResponseEntity<String> runJob(Job job, String jobName) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("createTime", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
            return ResponseEntity.ok(jobName + " triggered successfully");
        } catch (Exception e) {
            log.error("Failed to trigger {}", jobName, e);
            return ResponseEntity.internalServerError().body("Failed to trigger " + jobName + ": " + e.getMessage());
        }
    }
}
