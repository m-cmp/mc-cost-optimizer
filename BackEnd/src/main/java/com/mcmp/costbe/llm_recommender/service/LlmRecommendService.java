package com.mcmp.costbe.llm_recommender.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmp.costbe.llm_recommender.model.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LlmRecommendService {

    /** Default provider bean when the request omits one (back-compat with provider-less callers). */
    static final String DEFAULT_PROVIDER = "google";

    @Autowired private ScoreProvider scoreProvider;
    /** All LlmProvider beans, keyed by bean name: "google", "openai", "anthropic". */
    @Autowired private Map<String, LlmProvider> providers;
    @Autowired private PromptBuilder promptBuilder;
    @Autowired private RecommendationParser parser;
    @Autowired private RecommendRateLimiter rateLimiter;

    private final ObjectMapper om = new ObjectMapper();

    public Recommendation recommend(String instanceId, String provider, String model, String userQuestion) {
        LlmProvider llm;
        try {
            llm = resolveProvider(provider);
        } catch (IllegalArgumentException e) {
            return Recommendation.error(instanceId, "Unknown provider.");
        }

        // Cost guard (fail closed): never call the score/LLM provider when over the per-minute budget.
        if (!rateLimiter.tryAcquire()) {
            return Recommendation.error(instanceId, "Too many requests right now. Please try again shortly.");
        }

        try {
            String scoreJson = scoreProvider.get(instanceId);

            if (isInsufficient(scoreJson)) {
                return Recommendation.insufficient(instanceId);
            }

            String system = promptBuilder.systemPrompt();
            String user = promptBuilder.userPrompt(scoreJson, userQuestion);

            Recommendation r = generateAndParse(llm, system, user, model);
            r.setInstance(instanceId); // never trust the model's echoed id
            return r;

        } catch (RecommendationParseException e) {
            // both attempts failed to parse
            return Recommendation.error(instanceId, "Model returned an unparseable response.");
        } catch (Exception e) {
            // provider/transport failure — do not leak details (may contain keys)
            return Recommendation.error(instanceId, "Recommendation failed (provider error).");
        }
    }

    /** @throws IllegalArgumentException if the provider key is not a registered bean. */
    private LlmProvider resolveProvider(String provider) {
        String key = (provider == null || provider.isBlank()) ? DEFAULT_PROVIDER : provider;
        LlmProvider p = providers.get(key);
        if (p == null) {
            throw new IllegalArgumentException("Unknown provider: " + key);
        }
        return p;
    }

    private Recommendation generateAndParse(LlmProvider llm, String system, String user, String model) {
        String first = llm.generate(system, user, model);
        try {
            return parser.parse(first);
        } catch (RecommendationParseException firstFail) {
            // Cost guard: a blank response is usually a refusal/quota cutoff and a
            // retry almost always returns blank again — don't pay for a second call.
            if (first == null || first.isBlank()) {
                throw firstFail;
            }
            // Non-blank but unparseable -> retry once (spec §9).
            return parser.parse(llm.generate(system, user, model));
        }
    }

    private boolean isInsufficient(String scoreJson) {
        try {
            JsonNode n = om.readTree(scoreJson);
            JsonNode sig = n.get("action_signal");
            return sig != null && "insufficient_data".equals(sig.asText());
        } catch (Exception e) {
            return false; // malformed score is handled downstream as an error
        }
    }
}
