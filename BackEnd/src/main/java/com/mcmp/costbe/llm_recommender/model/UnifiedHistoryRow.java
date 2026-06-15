package com.mcmp.costbe.llm_recommender.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * One row of the unified recommendation-history grid (AlarmHistoryTable 7 columns).
 * Populated from either alarm_history (ML) or recommendation_history (LLM).
 */
@Getter
@Setter
public class UnifiedHistoryRow {

    private String date;          // 'yyyy-MM-dd HH:mm:ss' (string, sortable)
    private String csp;
    private String resourceId;
    private String resourceType;
    private String alarmType;     // "ML" | "LLM"
    private String alarmMessage;
    private String recommendType; // upsize|downsize|migrate|terminate|keep

    /** LLM rows only: raw response_json, used to derive alarmMessage; never serialized. */
    @JsonIgnore
    private String responseJson;
}
