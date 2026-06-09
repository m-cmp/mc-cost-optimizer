package com.mcmp.costbe.llm_recommender.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "API 키 저장 요청 DTO")
public class ApiKeySaveReqModel {

    @Schema(description = "유저 ID", example = "mcmpcostopti", required = true)
    private String userId;

    @Schema(description = "평문 API 키 (저장 후 즉시 폐기)", required = true)
    private String plainKey;
}
