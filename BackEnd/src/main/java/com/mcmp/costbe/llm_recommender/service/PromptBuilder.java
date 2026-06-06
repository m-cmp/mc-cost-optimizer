package com.mcmp.costbe.llm_recommender.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Service
public class PromptBuilder {

    private final String guide = loadGuide();

    public String systemPrompt() {
        return "You are a cloud compute resizing advisor. Given ONE instance's "
            + "resource-analysis score JSON, decide a resizing recommendation.\n\n"
            + "How to read the score (data guide):\n"
            + guide
            + "\n\nRules:\n"
            + "- Policy is asymmetric: upsizing is aggressive (P99-sensitive), "
            + "downsizing is conservative (both P95 and P99 must be low).\n"
            + "- Read action_signal together with daily_shape; action_signal is a "
            + "first signal, not the final decision — you decide.\n"
            + "- If data is insufficient, do not invent numbers.\n\n"
            + "Output contract (MANDATORY):\n"
            + "- Respond with a SINGLE JSON object and nothing else (no markdown, no prose).\n"
            + "- Exact fields: \"instance\", \"recommendation\", \"detail\", \"reasoning\", \"confidence\".\n"
            + "- \"recommendation\" is one of: terminate, downsize, upsize, migrate, keep.\n"
            + "- \"confidence\" is one of: high, medium, low.\n"
            + "- \"detail\" = one English sentence with the concrete suggested action.\n"
            + "- \"reasoning\" = English justification grounded in the metrics/shape provided.\n"
            + "- All text in English.";
    }

    public String userPrompt(String scoreJson) {
        return "Score JSON for one instance:\n" + scoreJson;
    }

    private String loadGuide() {
        try {
            return StreamUtils.copyToString(
                new ClassPathResource("llm/LLM_DATA_GUIDE.md").getInputStream(),
                StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Guide is bundled; if missing, fail loud at startup rather than ship a blind prompt.
            throw new IllegalStateException("Missing classpath resource llm/LLM_DATA_GUIDE.md", e);
        }
    }
}
