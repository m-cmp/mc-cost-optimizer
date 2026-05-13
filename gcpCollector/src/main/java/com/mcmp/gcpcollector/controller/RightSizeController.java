package com.mcmp.gcpcollector.controller;

import com.mcmp.gcpcollector.service.GcpRightSizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/rightsize")
@RequiredArgsConstructor
public class RightSizeController {

    private final GcpRightSizeService gcpRightSizeService;

    /**
     * GCP VM 사이즈 최적화 즉시 실행
     *
     * GET /admin/rightsize/detect           → Up + Down 전체
     * GET /admin/rightsize/detect?direction=up   → Size Up 만
     * GET /admin/rightsize/detect?direction=down → Size Down 만
     */
    @GetMapping("/detect")
    public ResponseEntity<Map<String, Object>> detect(
            @RequestParam(required = false) String direction) {

        if (direction != null && !direction.equalsIgnoreCase("up") && !direction.equalsIgnoreCase("down")) {
            return ResponseEntity.badRequest().body(Map.of("error", "direction은 'up' 또는 'down' 만 허용합니다."));
        }

        try {
            String normalizedDirection = direction != null ? capitalize(direction) : null;
            int count = gcpRightSizeService.detect(normalizedDirection);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("direction", normalizedDirection == null ? "ALL" : normalizedDirection);
            response.put("alarmSent", count);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("GCP VM 사이즈 최적화 실행 실패", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
