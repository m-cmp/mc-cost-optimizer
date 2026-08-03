package com.mcmp.costbe.batchtrigger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * CSP별 미사용/이상비용/자원추천 배치를 배치 주기(Quartz cron) 대기 없이 즉시 실행하는 테스트용 엔드포인트.
 * 결과는 각 배치가 기존과 동일하게 AlarmService(mail/slack)로 발송한다 - 이 컨트롤러는 트리거 역할만 한다.
 */
@RestController
@RequestMapping(path = "/api/costopti/be/batch")
@RequiredArgsConstructor
@Tag(name = "Batch Trigger", description = "CSP별 미사용/이상비용/자원추천 배치 즉시실행")
public class BatchTriggerController {

    private final BatchTriggerService batchTriggerService;

    @PostMapping("/aws/run")
    @Operation(summary = "AWS 배치 즉시실행", description = "unusedProcessJob(+ Resize 추천), abnormalProcessJob 을 즉시 실행한다.")
    public ResponseEntity<Map<String, String>> runAws() {
        return ResponseEntity.ok(batchTriggerService.triggerAws());
    }

    @PostMapping("/azure/run")
    @Operation(summary = "Azure 배치 즉시실행", description = "Unused/Anomaly/Recommend 배치를 즉시 실행한다.")
    public ResponseEntity<Map<String, String>> runAzure() {
        return ResponseEntity.ok(batchTriggerService.triggerAzure());
    }

    @PostMapping("/ncp/run")
    @Operation(summary = "NCP 배치 즉시실행", description = "Unused/Anomaly/Recommend 배치를 즉시 실행한다.")
    public ResponseEntity<Map<String, String>> runNcp() {
        return ResponseEntity.ok(batchTriggerService.triggerNcp());
    }

    @PostMapping("/gcp/run")
    @Operation(summary = "GCP 배치 즉시실행", description = "Unused/Anomaly/Recommend 탐지를 즉시 실행한다.")
    public ResponseEntity<Map<String, String>> runGcp() {
        return ResponseEntity.ok(batchTriggerService.triggerGcp());
    }
}
