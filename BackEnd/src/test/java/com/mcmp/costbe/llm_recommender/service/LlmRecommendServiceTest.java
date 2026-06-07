package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.model.Recommendation;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LlmRecommendServiceTest {

    /** Single-provider service registered under the default key ("google"). */
    private LlmRecommendService service(ScoreProvider score, LlmProvider llm) {
        return service(score, Map.of(LlmRecommendService.DEFAULT_PROVIDER, llm));
    }

    private LlmRecommendService service(ScoreProvider score, Map<String, LlmProvider> providers) {
        return service(score, providers, limiterOf(1_000_000)); // effectively unlimited
    }

    private LlmRecommendService service(ScoreProvider score, Map<String, LlmProvider> providers,
                                        RecommendRateLimiter rateLimiter) {
        LlmRecommendService s = new LlmRecommendService();
        ReflectionTestUtils.setField(s, "scoreProvider", score);
        ReflectionTestUtils.setField(s, "providers", providers);
        ReflectionTestUtils.setField(s, "promptBuilder", new PromptBuilder());
        ReflectionTestUtils.setField(s, "parser", new RecommendationParser());
        ReflectionTestUtils.setField(s, "rateLimiter", rateLimiter);
        return s;
    }

    private RecommendRateLimiter limiterOf(int perMin) {
        RecommendRateLimiter rl = new RecommendRateLimiter();
        ReflectionTestUtils.setField(rl, "limitPerMin", perMin);
        return rl;
    }

    private static final String VALID =
        "{\"instance\":\"x\",\"recommendation\":\"downsize\",\"detail\":\"d\","
        + "\"reasoning\":\"r\",\"confidence\":\"high\"}";

    @Test
    void insufficientData_skipsLlm() {
        List<String> calls = new ArrayList<>();
        ScoreProvider score = id -> "{\"action_signal\":\"insufficient_data\"}";
        LlmProvider llm = (s, u, m) -> { calls.add("called"); return VALID; };

        Recommendation r = service(score, llm).recommend("i-1", null, null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_INSUFFICIENT);
        assertThat(calls).isEmpty(); // LLM never called
    }

    @Test
    void happyPath_parsesAndForcesInstanceId() {
        ScoreProvider score = id -> "{\"action_signal\":\"downsize\"}";
        LlmProvider llm = (s, u, m) -> VALID;

        Recommendation r = service(score, llm).recommend("i-real", null, null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_OK);
        assertThat(r.getRecommendation()).isEqualTo("downsize");
        assertThat(r.getInstance()).isEqualTo("i-real"); // forced, not "x"
    }

    @Test
    void parseFailure_retriesOnce_thenSucceeds() {
        int[] n = {0};
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider llm = (s, u, m) -> (n[0]++ == 0) ? "garbage" : VALID;

        Recommendation r = service(score, llm).recommend("i-2", null, null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_OK);
        assertThat(n[0]).isEqualTo(2); // one retry
    }

    @Test
    void parseFailureTwice_returnsError() {
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider llm = (s, u, m) -> "still garbage";

        Recommendation r = service(score, llm).recommend("i-3", null, null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_ERROR);
        assertThat(r.getInstance()).isEqualTo("i-3");
    }

    @Test
    void providerThrows_returnsErrorWithoutLeaking() {
        ScoreProvider score = id -> "{\"action_signal\":\"upsize\"}";
        LlmProvider llm = (s, u, m) -> { throw new RuntimeException("key=SECRET timeout"); };

        Recommendation r = service(score, llm).recommend("i-4", null, null, null);

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

        Recommendation r = service(score, llm).recommend("i-q", null, null, "Can I save cost?");

        assertThat(capturedUserPrompt[0]).contains("Can I save cost?"); // question reached the prompt
        assertThat(r.getAnswer()).isEqualTo("Cost is already efficient.");
        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_OK);
    }

    @Test
    void selectsProviderByKey() {
        List<String> used = new ArrayList<>();
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider google = (s, u, m) -> { used.add("google"); return VALID; };
        LlmProvider anthropic = (s, u, m) -> { used.add("anthropic"); return VALID; };
        Map<String, LlmProvider> providers = Map.of("google", google, "anthropic", anthropic);

        service(score, providers).recommend("i-1", "anthropic", null, null);

        assertThat(used).containsExactly("anthropic"); // routed to the requested provider only
    }

    @Test
    void nullProvider_fallsBackToDefault() {
        List<String> used = new ArrayList<>();
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider google = (s, u, m) -> { used.add("google"); return VALID; };
        LlmProvider anthropic = (s, u, m) -> { used.add("anthropic"); return VALID; };
        Map<String, LlmProvider> providers = Map.of("google", google, "anthropic", anthropic);

        service(score, providers).recommend("i-1", null, null, null);

        assertThat(used).containsExactly("google"); // DEFAULT_PROVIDER
    }

    @Test
    void unknownProvider_returnsErrorWithoutCallingScore() {
        int[] scoreCalls = {0};
        ScoreProvider score = id -> { scoreCalls[0]++; return "{\"action_signal\":\"keep\"}"; };
        LlmProvider google = (s, u, m) -> VALID;

        Recommendation r = service(score, Map.of("google", google)).recommend("i-x", "nope", null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_ERROR);
        assertThat(scoreCalls[0]).isZero(); // bailed before doing any work
    }

    @Test
    void blankResponse_isNotRetried() {
        int[] n = {0};
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider llm = (s, u, m) -> { n[0]++; return ""; }; // blank every time

        Recommendation r = service(score, llm).recommend("i-blank", null, null, null);

        assertThat(r.getStatus()).isEqualTo(Recommendation.STATUS_ERROR);
        assertThat(n[0]).isEqualTo(1); // blank -> no second (paid) call (cost guard)
    }

    @Test
    void rateLimitExceeded_failsClosedWithoutCallingLlm() {
        List<String> calls = new ArrayList<>();
        ScoreProvider score = id -> "{\"action_signal\":\"keep\"}";
        LlmProvider llm = (s, u, m) -> { calls.add("call"); return VALID; };
        Map<String, LlmProvider> providers = Map.of(LlmRecommendService.DEFAULT_PROVIDER, llm);
        LlmRecommendService s = service(score, providers, limiterOf(1)); // budget = 1/min

        Recommendation r1 = s.recommend("i-1", null, null, null); // allowed
        Recommendation r2 = s.recommend("i-2", null, null, null); // over budget

        assertThat(r1.getStatus()).isEqualTo(Recommendation.STATUS_OK);
        assertThat(r2.getStatus()).isEqualTo(Recommendation.STATUS_ERROR);
        assertThat(calls).hasSize(1); // second request never reached the LLM
    }
}
