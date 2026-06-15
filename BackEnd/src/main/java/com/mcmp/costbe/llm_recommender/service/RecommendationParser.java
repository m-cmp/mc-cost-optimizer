package com.mcmp.costbe.llm_recommender.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmp.costbe.llm_recommender.model.Recommendation;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RecommendationParser {

    private static final Set<String> RECS =
        Set.of("terminate", "downsize", "upsize", "migrate", "keep");
    private static final Set<String> CONFS = Set.of("high", "medium", "low");

    private final ObjectMapper om = new ObjectMapper();

    public Recommendation parse(String rawText) {
        String json = stripFences(rawText);
        JsonNode node;
        try {
            node = om.readTree(json);
        } catch (Exception e) {
            throw new RecommendationParseException("not valid JSON");
        }
        if (node == null || !node.isObject()) {
            throw new RecommendationParseException("response is not a JSON object");
        }
        String rec = text(node, "recommendation");
        String conf = text(node, "confidence");
        if (rec == null || !RECS.contains(rec)) {
            throw new RecommendationParseException("invalid recommendation enum: " + rec);
        }
        if (conf == null || !CONFS.contains(conf)) {
            throw new RecommendationParseException("invalid confidence enum: " + conf);
        }
        Recommendation r = new Recommendation();
        r.setInstance(text(node, "instance"));
        r.setRecommendation(rec);
        r.setDetail(text(node, "detail"));
        r.setReasoning(text(node, "reasoning"));
        r.setConfidence(conf);
        r.setAnswer(text(node, "answer")); // optional (feature #2); null when absent, not validated
        r.setStatus(Recommendation.STATUS_OK);
        return r;
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    /** Remove ```json ... ``` fences and surrounding whitespace if present. */
    private String stripFences(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.startsWith("```")) {
            int firstNl = t.indexOf('\n');
            if (firstNl >= 0) t = t.substring(firstNl + 1);
            if (t.endsWith("```")) t = t.substring(0, t.length() - 3);
        }
        return t.trim();
    }
}
