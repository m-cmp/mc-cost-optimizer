package com.mcmp.costbe.usage.model.bill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BillingAssetChildModel {
    @Schema(description = "하위 리소스 코드", example = "AmazonEC2")
    private String childProductCode;
    @Schema(description = "리소스 양", example = "12")
    private Integer unit;
    @Schema(description = "비용", example = "13.0")
    private double bill;
}
