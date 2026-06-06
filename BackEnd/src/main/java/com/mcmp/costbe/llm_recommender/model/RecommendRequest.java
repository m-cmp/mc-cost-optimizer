package com.mcmp.costbe.llm_recommender.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendRequest {
    private String instanceId;
    private String model; // optional; null/blank -> provider default
}
