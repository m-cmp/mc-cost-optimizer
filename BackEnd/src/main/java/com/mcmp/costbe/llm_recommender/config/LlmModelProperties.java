package com.mcmp.costbe.llm_recommender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Selectable model catalog, bound from {@code llm.models.<provider>=a,b,c} in
 * application.properties. Exposed via GET /models so the frontend dropdown is
 * configuration-driven — adding/removing a model needs only a property change
 * (+ restart), not a frontend rebuild.
 */
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LlmModelProperties {

    /** providerKey ("google"/"openai"/"anthropic") -> ordered model ids (first = default). */
    private Map<String, List<String>> models = new LinkedHashMap<>();

    public Map<String, List<String>> getModels() {
        return models;
    }

    public void setModels(Map<String, List<String>> models) {
        this.models = models;
    }
}
