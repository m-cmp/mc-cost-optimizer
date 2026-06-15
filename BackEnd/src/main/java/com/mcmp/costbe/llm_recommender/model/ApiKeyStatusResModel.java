package com.mcmp.costbe.llm_recommender.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "API 키 등록 상태 응답 (원문 반환 없음)")
public class ApiKeyStatusResModel {

    @Schema(description = "프로바이더", example = "openai")
    private String provider;

    @Schema(description = "키 등록 여부")
    private boolean registered;
}
