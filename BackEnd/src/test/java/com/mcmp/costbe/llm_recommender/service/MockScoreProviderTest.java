package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.model.score.ScoreRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockScoreProviderTest {

    private final MockScoreProvider provider = new MockScoreProvider();

    private static ScoreRequest req(String instanceId) {
        ScoreRequest r = new ScoreRequest();
        r.setInstanceId(instanceId);
        return r;
    }

    @Test
    void returnsDownsizeSampleForKnownId() {
        String json = provider.get(req("i-demo-downsize"));
        assertThat(json).contains("\"action_signal\"").contains("downsize");
    }

    @Test
    void returnsInsufficientSampleForInsufficientId() {
        String json = provider.get(req("i-demo-insufficient"));
        assertThat(json).contains("insufficient_data");
    }

    @Test
    void unknownIdFallsBackToKeepSample() {
        String json = provider.get(req("i-totally-unknown"));
        assertThat(json).contains("\"action_signal\"");
    }
}
