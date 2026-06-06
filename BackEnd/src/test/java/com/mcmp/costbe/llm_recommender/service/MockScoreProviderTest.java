package com.mcmp.costbe.llm_recommender.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MockScoreProviderTest {

    private final MockScoreProvider provider = new MockScoreProvider();

    @Test
    void returnsDownsizeSampleForKnownId() {
        String json = provider.get("i-demo-downsize");
        assertThat(json).contains("\"action_signal\"").contains("downsize");
    }

    @Test
    void returnsInsufficientSampleForInsufficientId() {
        String json = provider.get("i-demo-insufficient");
        assertThat(json).contains("insufficient_data");
    }

    @Test
    void unknownIdFallsBackToKeepSample() {
        String json = provider.get("i-totally-unknown");
        assertThat(json).contains("\"action_signal\"");
    }
}
