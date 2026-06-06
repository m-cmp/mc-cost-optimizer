package com.mcmp.costbe.llm_recommender.service;

public interface LlmProvider {
    /** @param model nullable -> provider default. @return raw model text. */
    String generate(String system, String user, String model);
}
