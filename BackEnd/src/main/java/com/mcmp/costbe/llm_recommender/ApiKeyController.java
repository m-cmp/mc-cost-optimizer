package com.mcmp.costbe.llm_recommender;

import com.mcmp.costbe.llm_recommender.model.ApiKeySaveReqModel;
import com.mcmp.costbe.llm_recommender.model.ApiKeyStatusResModel;
import com.mcmp.costbe.llm_recommender.model.ApiKeyValidateResModel;
import com.mcmp.costbe.llm_recommender.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/costopti/be/llm_recommender/apikey")
@Tag(name = "API Key Management", description = "LLM 프로바이더 API 키 관리 (AES-256-GCM 암호화)")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/{provider}")
    @Operation(summary = "API 키 저장", description = "평문 키를 AES-256-GCM으로 암호화 후 DB에 upsert. 평문은 서버 메모리에도 남지 않음.")
    public ResponseEntity<Void> saveApiKey(
            @PathVariable String provider,
            @RequestBody ApiKeySaveReqModel req) {
        apiKeyService.saveApiKey(provider, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{provider}/status")
    @Operation(summary = "API 키 등록 여부 확인", description = "키 등록 여부(registered)만 반환. 원문·암호문은 응답에 포함되지 않음.")
    public ApiKeyStatusResModel getApiKeyStatus(
            @PathVariable String provider,
            @RequestParam String nsId) {
        return apiKeyService.getApiKeyStatus(provider, nsId);
    }

    @PostMapping("/{provider}/validate")
    @Operation(summary = "API 키 유효성 검증", description = "저장된 키를 복호화하여 프로바이더에 테스트 호출. 복호화된 키는 검증 후 즉시 폐기.")
    public ApiKeyValidateResModel validateApiKey(
            @PathVariable String provider,
            @RequestParam String nsId) {
        return apiKeyService.validateApiKey(provider, nsId);
    }

    @DeleteMapping("/{provider}")
    @Operation(summary = "API 키 삭제")
    public ResponseEntity<Void> deleteApiKey(
            @PathVariable String provider,
            @RequestParam String nsId) {
        apiKeyService.deleteApiKey(provider, nsId);
        return ResponseEntity.ok().build();
    }
}
