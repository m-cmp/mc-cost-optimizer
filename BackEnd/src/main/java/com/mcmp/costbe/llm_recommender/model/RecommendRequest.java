package com.mcmp.costbe.llm_recommender.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendRequest {
    private String nsId;         // 네임스페이스 ID — API 키 조회·이력 구분에 사용 (필수)
    private String instanceId;
    private String provider;     // optional; null/blank -> default provider (google)
    private String model;        // optional; null/blank -> provider default
    private String userQuestion; // optional free-form question; null/blank -> no answer
}
