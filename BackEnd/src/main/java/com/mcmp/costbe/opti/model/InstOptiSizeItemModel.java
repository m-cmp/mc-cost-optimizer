package com.mcmp.costbe.opti.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class InstOptiSizeItemModel {
    @Schema(description = "리소스 ID", example = "i-02eeeeeeee1234")
    private String resource_id;
    @Schema(description = "csp 종류", example = "AWS")
    private String csp_type;
    @Schema(description = "계정", example = "111111111111")
    private String account;
    @Schema(description = "기존타입", example = "t2.small")
    private String origin_type;
    @Schema(description = "추천타입", example = "t2.micro")
    private String rcmd_type;
    @Schema(description = "추천 방향", example = "Down")
    private String plan_type;
    @Schema(description = "기존타입 비용", example = "0.0288")
    private double origin_usd;
    @Schema(description = "추천타입 비용", example = "0.0144")
    private double rcmd_usd;
}
