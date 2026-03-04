package com.mcmp.gcpcollector.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GcpBillingScheduler {

    private final JobLauncher jobLauncher;
    private final Job gcpBillingJob;

    /**
     * 매일 전날 빌링 데이터 수집 → monthly 갱신 → 이상비용/미사용/예산 탐지
     */
    @Scheduled(cron = "${gcpBillingCronSchedule}")
    public void collectAndDetect() {
        String date = LocalDate.now().minusDays(1).toString();
        log.info("GCP 빌링 배치 시작 - date: {}", date);
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("date", date)
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(gcpBillingJob, params);
            log.info("GCP 빌링 배치 완료 - status: {}", execution.getStatus());
        } catch (Exception e) {
            log.error("GCP 빌링 배치 실패 - date: {}", date, e);
        }
    }
}
