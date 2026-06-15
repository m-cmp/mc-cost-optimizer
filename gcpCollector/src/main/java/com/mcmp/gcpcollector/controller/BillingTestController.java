package com.mcmp.gcpcollector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BillingTestController {

    private final JobLauncher jobLauncher;
    private final Job gcpBillingJob;

    /**
     * GCP 빌링 수집 + 이상비용/미사용자원/예산 탐지 즉시 실행
     * GET /test/billing/collect
     * GET /test/billing/collect?date=2026-02-19
     */
    @GetMapping("/admin/billing/collect")
    public ResponseEntity<Map<String, Object>> testCollect(
            @RequestParam(defaultValue = "") String date) {

        if (date.isEmpty()) {
            date = LocalDate.now().minusDays(1).toString();
        }

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("date", date)
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(gcpBillingJob, params);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("date", date);
            response.put("status", execution.getStatus().toString());
            response.put("jobId", execution.getJobId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("GCP 빌링 배치 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
