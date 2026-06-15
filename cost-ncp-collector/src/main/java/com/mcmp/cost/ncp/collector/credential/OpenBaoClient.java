package com.mcmp.cost.ncp.collector.credential;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OpenBao(Vault 호환) KV v2 에서 CSP 크레덴셜을 읽는 클라이언트.
 * 경로: {VAULT_ADDR}/v1/secret/data/csp/{provider} (admin holder 기준)
 */
@Slf4j
@Component
public class OpenBaoClient {

    private final String address;
    private final String token;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    public OpenBaoClient(@Value("${openbao.address:}") String address,
                         @Value("${openbao.token:}") String token) {
        this.address = address;
        this.token = token;
    }

    public boolean isConfigured() {
        return address != null && !address.isBlank() && token != null && !token.isBlank();
    }

    public Map<String, String> readCsp(String provider) {
        if (!isConfigured()) {
            return Collections.emptyMap();
        }
        return cache.computeIfAbsent(provider, this::fetch);
    }

    private Map<String, String> fetch(String provider) {
        String url = address.replaceAll("/+$", "") + "/v1/secret/data/csp/" + provider;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Vault-Token", token);
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

            JsonNode body = res.getBody();
            JsonNode data = (body != null) ? body.path("data").path("data") : null;
            if (data == null || data.isMissingNode() || !data.isObject()) {
                log.warn("OpenBao: secret/data/csp/{} 에 데이터가 없습니다.", provider);
                return Collections.emptyMap();
            }
            Map<String, String> result = new HashMap<>();
            data.fields().forEachRemaining(e -> result.put(e.getKey(), e.getValue().asText()));
            log.info("OpenBao: csp/{} 크레덴셜 {}개 키 로드", provider, result.size());
            return result;
        } catch (Exception e) {
            log.warn("OpenBao 조회 실패 (csp/{}): {}", provider, e.getMessage());
            return Collections.emptyMap();
        }
    }
}
