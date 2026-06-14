package com.mcmp.costbe.llm_recommender.service;

import org.springframework.stereotype.Component;

/** Maps the ML rightsizer's plan vocabulary to the LLM 5-enum (upsize/downsize/migrate/terminate/keep). */
@Component
public class RecommendTypeMapper {

    public String toEnum(String plan) {
        if (plan == null) return "-";
        switch (plan.trim().toLowerCase()) {
            case "up":
            case "상향":
                return "upsize";
            case "down":
            case "하향":
                return "downsize";
            case "modernize":
            case "최신화":
                return "migrate";
            case "unused":
            case "미사용":
                return "terminate";
            default:
                return plan; // unknown value passes through unchanged
        }
    }
}
