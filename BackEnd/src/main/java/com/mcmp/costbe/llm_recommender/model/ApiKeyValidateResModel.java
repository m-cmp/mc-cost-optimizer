package com.mcmp.costbe.llm_recommender.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "API 키 유효성 검증 결과")
public class ApiKeyValidateResModel {

    @Schema(description = "프로바이더", example = "openai")
    private String provider;

    @Schema(description = "유효 여부")
    private boolean valid;

    @Schema(description = "결과 메시지", example = "OK")
    private String message;
}
