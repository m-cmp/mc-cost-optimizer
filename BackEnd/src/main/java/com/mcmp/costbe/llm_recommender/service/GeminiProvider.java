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

@Component("google")
public class GeminiProvider implements LlmProvider {

    @Value("${llm.gemini.base-url}") private String baseUrl;
    @Value("${llm.gemini.model}") private String defaultModel;
    @Value("${llm.gemini.temperature:0.2}") private double temperature;
    @Value("${llm.gemini.max-output-tokens:2048}") private int maxOutputTokens;

    @Autowired
    @Qualifier("llmRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private ApiKeyService apiKeyService;

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public String generate(String system, String user, String model, String nsId) {
        String apiKey = apiKeyService.decryptApiKey("google", nsId);
        String m = (model == null || model.isBlank()) ? defaultModel : model;
        String url = baseUrl + "/models/" + m + ":generateContent";

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", system))),
                "contents", List.of(Map.of("parts", List.of(Map.of("text", user)))),
                "generationConfig", Map.of(
                        "temperature", temperature,
                        "maxOutputTokens", maxOutputTokens,
                        "responseMimeType", "application/json")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        String resp = restTemplate.postForObject(url, new HttpEntity<>(body, headers), String.class);
        return extractText(resp);
    }

    /** candidates[0].content.parts[0].text */
    private String extractText(String resp) {
        try {
            JsonNode root = om.readTree(resp);
            return root.path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text").asText();
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected Gemini response shape");
        }
    }
}
