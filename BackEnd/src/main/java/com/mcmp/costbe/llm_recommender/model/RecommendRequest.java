package com.mcmp.costbe.llm_recommender.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendRequest {
    private String userId;       // API 키 조회에 사용. null/blank -> env var 폴백
    private String instanceId;
    private String provider;     // optional; null/blank -> default provider (google)
    private String model;        // optional; null/blank -> provider default
    private String userQuestion; // optional free-form question; null/blank -> no answer
}
