package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.model.score.ScoreRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
class GeminiProviderIT {

    @Autowired private GeminiProvider gemini;
    @Autowired private PromptBuilder promptBuilder;
    @Autowired private RecommendationParser parser;
    @Autowired private ScoreProvider scoreProvider;

    @Test
    void realCall_returnsParseableRecommendation() {
        ScoreRequest sr = new ScoreRequest();
        sr.setInstanceId("i-demo-downsize");
        String score = scoreProvider.get(sr);
        String text = gemini.generate(
                promptBuilder.systemPrompt(), promptBuilder.userPrompt(score), null, null);
        var r = parser.parse(text);
        assertThat(r.getRecommendation()).isIn(
                "terminate", "downsize", "upsize", "migrate", "keep");
        assertThat(r.getConfidence()).isIn("high", "medium", "low");
    }
}
