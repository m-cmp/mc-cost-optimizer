package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.dao.ApiKeyDao;
import com.mcmp.costbe.llm_recommender.model.ApiKeySaveReqModel;
import com.mcmp.costbe.llm_recommender.model.ApiKeyStatusResModel;
import com.mcmp.costbe.llm_recommender.model.ApiKeyValidateResModel;
import com.mcmp.costbe.llm_recommender.model.ProviderKeyModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyDao apiKeyDao;

    @Value("${llm.key.master:}")
    private String masterKeyBase64;

    private static final int TAG_LENGTH_BIT  = 128;
    private static final int TAG_LENGTH_BYTE = 16;   // 128 / 8
    private static final int IV_LENGTH_BYTE  = 12;   // 96-bit nonce

    // 마스터키 유효성 검사 후 SecretKeySpec 반환
    private SecretKeySpec getMasterKey() {
        if (masterKeyBase64 == null || masterKeyBase64.isBlank()) {
            throw new IllegalStateException("LLM_KEY_MASTER 환경변수가 설정되지 않았습니다.");
        }
        byte[] keyBytes = Base64.getDecoder().decode(masterKeyBase64);
        if (keyBytes.length != 32) {
            throw new IllegalStateException("LLM_KEY_MASTER는 256-bit(32바이트)이어야 합니다.");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * 평문 API 키를 AES-256-GCM으로 암호화하여 DB에 upsert.
     * IV는 매 호출마다 새로 생성(재사용 금지).
     */
    public void saveApiKey(String provider, ApiKeySaveReqModel req) {
        SecretKeySpec secretKey = getMasterKey();
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);  // 암호화마다 새로 생성

        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            // Java GCM: doFinal 결과 = ciphertext + tag (tag는 마지막 TAG_LENGTH_BYTE)
            byte[] encryptedWithTag = cipher.doFinal(req.getPlainKey().getBytes(StandardCharsets.UTF_8));
            byte[] ciphertext = Arrays.copyOfRange(encryptedWithTag, 0, encryptedWithTag.length - TAG_LENGTH_BYTE);
            byte[] tag        = Arrays.copyOfRange(encryptedWithTag, encryptedWithTag.length - TAG_LENGTH_BYTE, encryptedWithTag.length);

            ProviderKeyModel model = new ProviderKeyModel();
            model.setNsId(req.getNsId());
            model.setProvider(provider);
            model.setEncKey(Base64.getEncoder().encodeToString(ciphertext));
            model.setIv(Base64.getEncoder().encodeToString(iv));
            model.setTag(Base64.getEncoder().encodeToString(tag));

            apiKeyDao.upsertApiKey(model);
        } catch (Exception e) {
            // 평문 키가 로그에 노출되지 않도록 메시지에 키 값 포함 금지
            throw new RuntimeException("API 키 암호화 중 오류 발생", e);
        }
    }

    /**
     * 키 등록 여부만 반환. 원문 키는 절대 응답에 포함하지 않음.
     */
    public ApiKeyStatusResModel getApiKeyStatus(String provider, String nsId) {
        ProviderKeyModel row = apiKeyDao.selectApiKey(Map.of("provider", provider, "nsId", nsId));
        return ApiKeyStatusResModel.builder()
                .provider(provider)
                .registered(row != null)
                .build();
    }

    /**
     * DB에서 키 삭제.
     */
    public void deleteApiKey(String provider, String nsId) {
        apiKeyDao.deleteApiKey(Map.of("provider", provider, "nsId", nsId));
    }

    /**
     * DB에 저장된 키를 복호화 후 프로바이더에 테스트 호출하여 유효성 검증.
     * 복호화된 키는 검증 직후 GC에 위임 (로그·캐시 저장 금지).
     */
    public ApiKeyValidateResModel validateApiKey(String provider, String nsId) {
        String plainKey;
        try {
            plainKey = decryptApiKey(provider, nsId);
        } catch (Exception e) {
            return ApiKeyValidateResModel.builder()
                    .provider(provider)
                    .valid(false)
                    .message("Key not found or decryption failed.")
                    .build();
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String url;
            Map<String, Object> body;

            switch (provider.toLowerCase()) {
                case "openai":
                    url = "https://api.openai.com/v1/chat/completions";
                    headers.set("Authorization", "Bearer " + plainKey);
                    body = Map.of(
                        "model", "gpt-4o-mini",
                        "messages", List.of(Map.of("role", "user", "content", "Hi")),
                        "max_tokens", 10
                    );
                    break;
                case "anthropic":
                    url = "https://api.anthropic.com/v1/messages";
                    headers.set("x-api-key", plainKey);
                    headers.set("anthropic-version", "2023-06-01");
                    body = Map.of(
                        "model", "claude-haiku-4-5-20251001",
                        "max_tokens", 10,
                        "messages", List.of(Map.of("role", "user", "content", "Hi"))
                    );
                    break;
                case "google":
                    url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + plainKey;
                    body = Map.of(
                        "contents", List.of(Map.of("parts", List.of(Map.of("text", "Hi"))))
                    );
                    break;
                default:
                    return ApiKeyValidateResModel.builder()
                            .provider(provider)
                            .valid(false)
                            .message("Unsupported provider: " + provider)
                            .build();
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String replyText = parseReplyText(provider, response.getBody());
            return ApiKeyValidateResModel.builder()
                    .provider(provider)
                    .valid(true)
                    .message("OK - Response: " + replyText)
                    .build();

        } catch (HttpClientErrorException e) {
            // 401/403 등 인증 실패
            return ApiKeyValidateResModel.builder()
                    .provider(provider)
                    .valid(false)
                    .message("Invalid API key. (" + e.getStatusCode() + ")")
                    .build();
        } catch (HttpServerErrorException e) {
            return ApiKeyValidateResModel.builder()
                    .provider(provider)
                    .valid(false)
                    .message("Provider server error. (" + e.getStatusCode() + ")")
                    .build();
        } catch (Exception e) {
            return ApiKeyValidateResModel.builder()
                    .provider(provider)
                    .valid(false)
                    .message("Validation request failed.")
                    .build();
        } finally {
            plainKey = null; // 복호화된 키 즉시 폐기
        }
    }

    /**
     * LLM 호출 직전에만 사용. 복호화된 키는 호출자가 사용 후 즉시 폐기해야 함.
     * 반환값을 로그·캐시에 저장 금지.
     */
    public String decryptApiKey(String provider, String nsId) {
        ProviderKeyModel row = apiKeyDao.selectApiKey(Map.of("provider", provider, "nsId", nsId));
        if (row == null) {
            throw new ApiKeyNotRegisteredException("등록된 API 키가 없습니다: " + provider);
        }

        SecretKeySpec secretKey = getMasterKey();

        try {
            byte[] iv         = Base64.getDecoder().decode(row.getIv());
            byte[] ciphertext = Base64.getDecoder().decode(row.getEncKey());
            byte[] tag        = Base64.getDecoder().decode(row.getTag());

            // ciphertext + tag 를 합쳐서 Java GCM에 전달 (tag 검증 포함)
            byte[] combined = new byte[ciphertext.length + tag.length];
            System.arraycopy(ciphertext, 0, combined, 0, ciphertext.length);
            System.arraycopy(tag, 0, combined, ciphertext.length, tag.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] decrypted = cipher.doFinal(combined);  // tag 불일치 시 AEADBadTagException 발생
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("API 키 복호화 중 오류 발생", e);
        }
    }

    // 프로바이더별 응답 JSON에서 생성된 텍스트 추출
    private String parseReplyText(String provider, String responseBody) {
        try {
            JsonNode root = new ObjectMapper().readTree(responseBody);
            switch (provider.toLowerCase()) {
                case "openai":
                    // choices[0].message.content
                    return root.path("choices").path(0).path("message").path("content").asText("(no content)");
                case "anthropic":
                    // content[0].text
                    return root.path("content").path(0).path("text").asText("(no content)");
                case "google":
                    // candidates[0].content.parts[0].text
                    return root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("(no content)");
                default:
                    return "(unknown provider)";
            }
        } catch (Exception e) {
            return "(parse error)";
        }
    }
}
