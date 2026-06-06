package com.mcmp.costbe.llm_recommender.service;

public interface ScoreProvider {
    /** @return the analysis-score JSON (as a string) for one instance. */
    String get(String instanceId);
}
