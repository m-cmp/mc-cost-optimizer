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

@Component("openai")
public class OpenAiProvider implements LlmProvider {

    @Value("${llm.openai.base-url}") private String baseUrl;
    @Value("${llm.openai.model}") private String defaultModel;
    @Value("${llm.openai.temperature:0.2}") private double temperature;
    @Value("${llm.openai.max-tokens:2048}") private int maxTokens;

    @Autowired
    @Qualifier("llmRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ApiKeyService apiKeyService;

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public String generate(String system, String user, String model, String nsId) {
        String apiKey = apiKeyService.decryptApiKey("openai", nsId);
        String m = (model == null || model.isBlank()) ? defaultModel : model;
        String url = baseUrl + "/chat/completions";

        Map<String, Object> body = Map.of(
                "model", m,
                "messages", List.of(
                        Map.of("role", "system", "content", system),
                        Map.of("role", "user", "content", user)),
                "temperature", temperature,
                "max_tokens", maxTokens,
                "response_format", Map.of("type", "json_object")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String resp = restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);
        return extractText(resp);
    }

    /** choices[0].message.content */
    private String extractText(String resp) {
        try {
            JsonNode root = om.readTree(resp);
            return root.path("choices").path(0)
                    .path("message").path("content")
                    .asText();
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected OpenAI response shape");
        }
    }
}
