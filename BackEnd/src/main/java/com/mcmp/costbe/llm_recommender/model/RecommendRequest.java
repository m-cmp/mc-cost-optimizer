package com.mcmp.costbe.llm_recommender.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendRequest {
    private String instanceId;
    private String provider; // optional; null/blank -> default provider (google). selects the LlmProvider bean.
    private String model; // optional; null/blank -> provider default
    private String userQuestion; // optional free-form question (feature #2); null/blank -> no answer
}
