package com.mcmp.costbe.llm_recommender.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component("anthropic")
public class AnthropicProvider implements LlmProvider {

    @Value("${llm.anthropic.base-url}") private String baseUrl;
    @Value("${llm.anthropic.model}") private String defaultModel;
    @Value("${llm.anthropic.version:2023-06-01}") private String anthropicVersion;
    @Value("${llm.anthropic.max-tokens:4096}") private int maxTokens;

    @Autowired
    @Qualifier("llmRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ApiKeyService apiKeyService;

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public String generate(String system, String user, String model, String userId) {
        String apiKey = apiKeyService.decryptApiKey("anthropic", userId);
        String m = (model == null || model.isBlank()) ? defaultModel : model;
        String url = baseUrl + "/messages";

        // No temperature: Opus 4.8/4.7 reject sampling params (400)
        Map<String, Object> body = Map.of(
                "model", m,
                "max_tokens", maxTokens,
                "system", system,
                "messages", List.of(Map.of("role", "user", "content", user))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", anthropicVersion);

        String resp = restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);
        return extractText(resp);
    }

    /** content[0].text */
    private String extractText(String resp) {
        try {
            JsonNode root = om.readTree(resp);
            return root.path("content").path(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected Anthropic response shape");
        }
    }
}
