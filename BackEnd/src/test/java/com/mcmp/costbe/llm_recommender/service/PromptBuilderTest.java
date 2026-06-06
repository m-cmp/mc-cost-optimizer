package com.mcmp.costbe.llm_recommender.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PromptBuilderTest {

    private final PromptBuilder builder = new PromptBuilder();

    @Test
    void systemPrompt_containsRole_outputContract_andGuide() {
        String system = builder.systemPrompt();
        // role
        assertThat(system).containsIgnoringCase("resizing");
        // output contract: exact spec field names + enums + English + JSON-only
        assertThat(system).contains("\"recommendation\"");
        assertThat(system).contains("terminate").contains("downsize")
                .contains("upsize").contains("migrate").contains("keep");
        assertThat(system).contains("\"confidence\"")
                .contains("high").contains("medium").contains("low");
        assertThat(system).containsIgnoringCase("JSON");
        // embedded guide marker (from LLM_DATA_GUIDE.md)
        assertThat(system).contains("action_signal");
    }

    @Test
    void userPrompt_embedsScoreJsonVerbatim() {
        String score = "{\"instance\":\"i-x\",\"action_signal\":\"downsize\"}";
        String user = builder.userPrompt(score);
        assertThat(user).contains(score);
    }
}
