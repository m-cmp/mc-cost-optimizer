package com.mcmp.costbe.llm_recommender.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmp.costbe.llm_recommender.model.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LlmRecommendService {

    @Autowired private ScoreProvider scoreProvider;
    @Autowired private LlmProvider llmProvider;
    @Autowired private PromptBuilder promptBuilder;
    @Autowired private RecommendationParser parser;

    private final ObjectMapper om = new ObjectMapper();

    public Recommendation recommend(String instanceId, String model, String userQuestion) {
        try {
            String scoreJson = scoreProvider.get(instanceId);

            if (isInsufficient(scoreJson)) {
                return Recommendation.insufficient(instanceId);
            }

            String system = promptBuilder.systemPrompt();
            String user = promptBuilder.userPrompt(scoreJson, userQuestion);

            Recommendation r = generateAndParse(system, user, model);
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

    private Recommendation generateAndParse(String system, String user, String model) {
        try {
            return parser.parse(llmProvider.generate(system, user, model));
        } catch (RecommendationParseException firstFail) {
            // retry once (spec §9)
            return parser.parse(llmProvider.generate(system, user, model));
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
