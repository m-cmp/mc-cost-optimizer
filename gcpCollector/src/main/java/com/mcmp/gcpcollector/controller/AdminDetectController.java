package com.mcmp.gcpcollector.controller;

import com.mcmp.gcpcollector.service.GcpAnomalyDetectionService;
import com.mcmp.gcpcollector.service.GcpBudgetCheckService;
import com.mcmp.gcpcollector.service.GcpUnusedDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * gcpBillingJob(BigQuery 수집 포함) 을 거치지 않고, 배치 주기와 무관하게
 * 이상비용/미사용자원/예산 체크 서비스를 즉시 실행하기 위한 관리용 엔드포인트.
 * 자원추천(Rightsizing) 즉시실행은 기존 RightSizeController(/admin/rightsize/detect) 사용.
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDetectController {

    private final GcpAnomalyDetectionService gcpAnomalyDetectionService;
    private final GcpUnusedDetectionService gcpUnusedDetectionService;
    private final GcpBudgetCheckService gcpBudgetCheckService;

    @GetMapping("/anomaly/detect")
    public ResponseEntity<String> detectAnomaly() {
        try {
            gcpAnomalyDetectionService.detect();
            return ResponseEntity.ok("GCP Anomaly Detection triggered successfully");
        } catch (Exception e) {
            log.error("GCP Anomaly Detection 실행 실패", e);
            return ResponseEntity.internalServerError().body("Failed to trigger anomaly detection: " + e.getMessage());
        }
    }

    @GetMapping("/unused/detect")
    public ResponseEntity<String> detectUnused() {
        try {
            gcpUnusedDetectionService.detect();
            return ResponseEntity.ok("GCP Unused Detection triggered successfully");
        } catch (Exception e) {
            log.error("GCP Unused Detection 실행 실패", e);
            return ResponseEntity.internalServerError().body("Failed to trigger unused detection: " + e.getMessage());
        }
    }

    @GetMapping("/budget/check")
    public ResponseEntity<String> checkBudget() {
        try {
            gcpBudgetCheckService.check();
            return ResponseEntity.ok("GCP Budget Check triggered successfully");
        } catch (Exception e) {
            log.error("GCP Budget Check 실행 실패", e);
            return ResponseEntity.internalServerError().body("Failed to trigger budget check: " + e.getMessage());
        }
    }
}
