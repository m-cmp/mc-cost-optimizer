package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.model.score.ScoreRequest;

public interface ScoreProvider {
    /** @return the analysis-score JSON (as a string) for one instance. */
    String get(ScoreRequest req);
}
