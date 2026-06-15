package com.mcmp.costbe.llm_recommender.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "프로바이더 API 키 DB 모델")
public class ProviderKeyModel {
    private Long id;
    private String nsId;
    private String provider;
    private String encKey;
    private String iv;
    private String tag;
}
