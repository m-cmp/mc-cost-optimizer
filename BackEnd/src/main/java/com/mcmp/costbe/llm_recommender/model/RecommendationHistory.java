package com.mcmp.costbe.llm_recommender.model;

import lombok.Getter;
import lombok.Setter;

/**
 * One row of recommendation_history (spec §8 — request/response audit trail).
 * id and created_at are DB-generated (AUTO_INCREMENT / DEFAULT CURRENT_TIMESTAMP),
 * so they are not set on insert.
 */
@Getter
@Setter
public class RecommendationHistory {

    private Long id;
    private String nsId;           // namespace ID — 프로젝트 단위 구분 (spec: ns_id)
    private String instanceId;
    private String recommendation; // nullable: insufficient_data / error rows have no recommendation
    private String responseJson;   // full Recommendation serialized as JSON (spec: response_json)
    private String createdAt;      // 조회 전용: DATE_FORMAT 문자열 (insert엔 미사용, DB가 생성)
}
