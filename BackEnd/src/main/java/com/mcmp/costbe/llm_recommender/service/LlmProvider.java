package com.mcmp.costbe.llm_recommender.service;

public interface LlmProvider {
    /** @param model nullable -> provider default. @param nsId DB 복호화 키 조회용(네임스페이스 단위). @return raw model text. */
    String generate(String system, String user, String model, String nsId);
}
