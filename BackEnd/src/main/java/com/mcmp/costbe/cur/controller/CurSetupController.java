package com.mcmp.costbe.cur.controller;

import com.mcmp.costbe.cur.dto.CurSetupResult;
import com.mcmp.costbe.cur.dto.CurSetupStatus;
import com.mcmp.costbe.cur.exception.CurCredentialNotFoundException;
import com.mcmp.costbe.cur.service.CurSetupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/aws/cur")
@RequiredArgsConstructor
public class CurSetupController {

    private final CurSetupService curSetupService;

    @PostMapping("/setup")
    public ResponseEntity<?> setup() {
        try {
            CurSetupResult result = curSetupService.setup();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("CUR Setup error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/mail-receiver")
    public ResponseEntity<?> saveMailReceiver(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            if (email == null || email.isBlank())
                return ResponseEntity.badRequest().body(Map.of("error", "email is required"));
            curSetupService.saveMailReceiver(email);
            return ResponseEntity.ok(Map.of("saved", email));
        } catch (Exception e) {
            log.error("Mail receiver save error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        try {
            CurSetupStatus status = curSetupService.getStatus();
            return ResponseEntity.ok(status);
        } catch (CurCredentialNotFoundException e) {
            return ResponseEntity.ok(new CurSetupStatus(false, false, null, null, null));
        } catch (Exception e) {
            log.error("CUR 상태 조회 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
