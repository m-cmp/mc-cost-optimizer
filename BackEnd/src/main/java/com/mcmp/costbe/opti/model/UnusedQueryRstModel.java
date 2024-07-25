package com.mcmp.costbe.opti.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UnusedQueryRstModel {
    @Schema(description = "cso", example = "AWS")
    private String csp;
    @Schema(description = "계정", example = "Auser")
    private String account;
    @Schema(description = "리소스 id", example = "resourceUniqueID")
    private String instance_id;
    @Schema(description = "미사용 추천 타입", example = "Unused")
    private String plan_type;
    @Schema(description = "절약 예상 비용(USD)", example = "10.0")
    private double saving_cost;
}
