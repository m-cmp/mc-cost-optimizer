package com.mcmp.costbe.llm_recommender.service;

import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

/**
 * v1 score source: returns bundled sample JSON. Replace by swapping in an
 * ML-API-backed ScoreProvider bean later (this stays as a fallback/dev bean).
 */
@Component
@Primary
public class MockScoreProvider implements ScoreProvider {

    @Override
    public String get(String instanceId) {
        String file;
        if (instanceId == null) {
            file = "keep";
        } else if (instanceId.contains("downsize")) {
            file = "downsize";
        } else if (instanceId.contains("upsize")) {
            file = "upsize";
        } else if (instanceId.contains("insufficient")) {
            file = "insufficient";
        } else {
            file = "keep";
        }
        return load("llm-samples/" + file + ".json");
    }

    private String load(String path) {
        try {
            return StreamUtils.copyToString(
                    new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Missing sample resource: " + path, e);
        }
    }
}
