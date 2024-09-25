package com.mcmp.costbe.opti.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AbnoramlItemModel {
    @Schema(description = "서비스 코드", example = "AmazonEC2")
    private String product_cd;

    @Schema(description = "이상비용 위험도", example = "Alarm")
    private String abnormal_rating;

    @Schema(description = "지난달 사용 차이률", example = "200%")
    private double percentage_point;

    @Schema(description = "오늘 과금 비용(USD)", example = "20.0")
    private double standard_cost;

    @Schema(description = "지난달 사용 과금 비용 평균(USD)", example = "10.0")
    private double subject_cost;

    @Schema(description = "csp 종류", example = "AWS")
    private String csp_type;

}
