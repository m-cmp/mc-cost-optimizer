package com.mcmp.costbe.llm_recommender.service;

public interface LlmProvider {
    /** @param model nullable -> provider default. @param userId DB 복호화 키 조회용; null/blank -> env var 폴백. @return raw model text. */
    String generate(String system, String user, String model, String userId);
}
