package com.mcmp.costbe.gcp.controller;

import com.mcmp.costbe.gcp.dto.GcpDatasetRequest;
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

    /**
     * csp/gcp 어드민 SA를 사용해 운영 SA 생성·역할 부여·기본 데이터셋 생성을 한 번에 수행.
     * 결과는 cost/gcp 에 저장되며 어드민 키는 csp/gcp 에 그대로 유지.
     */
    @PostMapping("/auto-provision")
    public ResponseEntity<?> autoProvision() {
        try {
            GcpSetupResult result = gcpSetupService.autoProvision();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("GCP 자동 프로비저닝 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /** 현재 셋업 단계 조회 */
    @GetMapping("/status")
    public ResponseEntity<GcpSetupStatus> status() {
        try {
            return ResponseEntity.ok(gcpSetupService.getStatus());
        } catch (Exception e) {
            log.error("GCP 상태 조회 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 데이터셋 이름 변경 (사용자가 직접 변경하는 경우에만 호출).
     * Body: { "action": "create"|"existing", "datasetName": "..." }
     */
    @PostMapping("/dataset")
    public ResponseEntity<?> saveDataset(@RequestBody GcpDatasetRequest req) {
        try {
            gcpSetupService.saveDataset(req.getAction(), req.getDatasetName());
            return ResponseEntity.ok(Map.of("status", "OK"));
        } catch (Exception e) {
            log.error("GCP 데이터셋 저장 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GCP 콘솔에서 빌링 내보내기 설정 완료 후 호출.
     * BigQuery 연결 확인 + 빌링 테이블 탐색 + confirmed 저장 + DB 등록.
     */
    @PostMapping("/billing-confirmed")
    public ResponseEntity<?> confirmBillingExport() {
        try {
            GcpSetupResult result = gcpSetupService.confirmBillingExport();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("GCP 빌링 확인 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
