package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.model.Recommendation;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LlmRecommendServiceTest {

    private LlmRecommendService service(ScoreProvider score, LlmProvider llm) {
        LlmRecommendService s = new LlmRecommendService();
        ReflectionTestUtils.setField(s, "scoreProvider", score);
        ReflectionTestUtils.setField(s, "llmProvider", llm);
        ReflectionTestUtils.setField(s, "promptBuilder", new PromptBuilder());
        ReflectionTestUtils.setField(s, "parser", new RecommendationParser());
        return s;
    }

    private static final String VALID =
        "{\"instance\":\"x\",\"recommendation\":\"downsize\",\"detail\":\"d\","
        + "\"reasoning\":\"r\",\"confidence\":\"high\"}";

    @Test
    void insufficientData_skipsLlm() {
        List<String> calls = new ArrayList<>();
        ScoreProvider score = id -> "{\"action_signal\":\"insufficient_data\"}";
        LlmProvider llm = (s, u, m) -> { calls.add("called"); return VALID; };

        Recommendation r = service(score, llm).recommend("i-1", null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_INSUFFICIENT);
        assertThat(calls).isEmpty(); // LLM never called
    }

    @Test
    void happyPath_parsesAndForcesInstanceId() {
        ScoreProvider score = id -> "{\"action_signal\":\"downsize\"}";
        LlmProvider llm = (s, u, m) -> VALID;

        Recommendation r = service(score, llm).recommend("i-real", null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_OK);
        assertThat(r.getRecommendation()).isEqualTo("downsize");
        assertThat(r.getInstance()).isEqualTo("i-real"); // forced, not "x"
    }

    @Test
    void parseFailure_retriesOnce_thenSucceeds() {
        int[] n = {0};
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider llm = (s, u, m) -> (n[0]++ == 0) ? "garbage" : VALID;

        Recommendation r = service(score, llm).recommend("i-2", null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_OK);
        assertThat(n[0]).isEqualTo(2); // one retry
    }

    @Test
    void parseFailureTwice_returnsError() {
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider llm = (s, u, m) -> "still garbage";

        Recommendation r = service(score, llm).recommend("i-3", null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_ERROR);
        assertThat(r.getInstance()).isEqualTo("i-3");
    }

    @Test
    void providerThrows_returnsErrorWithoutLeaking() {
        ScoreProvider score = id -> "{\"action_signal\":\"upsize\"}";
        LlmProvider llm = (s, u, m) -> { throw new RuntimeException("key=SECRET timeout"); };

        Recommendation r = service(score, llm).recommend("i-4", null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_ERROR);
        assertThat(r.getError()).doesNotContain("SECRET");
    }

    @Test
    void userQuestion_isPassedToPrompt_andAnswerParsed() {
        String[] capturedUserPrompt = {null};
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider llm = (s, u, m) -> {
            capturedUserPrompt[0] = u;
            return "{\"instance\":\"x\",\"recommendation\":\"keep\",\"detail\":\"d\","
                + "\"reasoning\":\"r\",\"confidence\":\"high\",\"answer\":\"Cost is already efficient.\"}";
        };

        Recommendation r = service(score, llm).recommend("i-q", null, "Can I save cost?");

        assertThat(capturedUserPrompt[0]).contains("Can I save cost?"); // question reached the prompt
        assertThat(r.getAnswer()).isEqualTo("Cost is already efficient.");
        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_OK);
    }
}
