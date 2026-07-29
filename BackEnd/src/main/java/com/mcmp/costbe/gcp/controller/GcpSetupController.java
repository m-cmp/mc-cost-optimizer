package com.mcmp.costbe.gcp.controller;

import com.mcmp.costbe.gcp.dto.GcpSetupResult;
import com.mcmp.costbe.gcp.dto.GcpSetupStatus;
import com.mcmp.costbe.gcp.service.GcpSetupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/gcp/setup")
@RequiredArgsConstructor
public class GcpSetupController {

    private final GcpSetupService gcpSetupService;

    @PostMapping
    public ResponseEntity<?> setup() {
        try {
            GcpSetupResult result = gcpSetupService.setup();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("GCP Setup error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        try {
            GcpSetupStatus status = gcpSetupService.getStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("GCP 상태 조회 오류: {}", e.getMessage(), e);
            return ResponseEntity.ok(new GcpSetupStatus(false));
        }
    }
}
