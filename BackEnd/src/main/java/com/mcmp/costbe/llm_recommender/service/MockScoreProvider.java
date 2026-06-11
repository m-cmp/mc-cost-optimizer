package com.mcmp.costbe.llm_recommender.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcmp.costbe.llm_recommender.model.score.ScoreRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

/**
 * v1 score source: returns bundled sample JSON. Replace by swapping in an
 * API-backed ScoreProvider bean (POST /score per LLM_DEV_SPEC.html section 4)
 * later (this stays as a fallback/dev bean).
 */
@Component
@Primary
public class MockScoreProvider implements ScoreProvider {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    public String get(ScoreRequest req) {
        String instanceId = req.getInstanceId();
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
        try {
            ObjectNode node = (ObjectNode) om.readTree(load("llm-samples/" + file + ".json"));
            node.put("instance", instanceId);
            if (req.getIntervalSeconds() != null) {
                node.put("interval_seconds", req.getIntervalSeconds());
            }
            return om.writeValueAsString(node);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build mock score response for: " + instanceId, e);
        }
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
