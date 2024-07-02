package com.mcmp.costbe.usage.model.bill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BillingAssetModel {
    @Schema(description = "상위 리소스 코드", example = "Virtual Machine")
    private String familyProductCode;
    @Schema(description = "하위 리소스 정보")
    private List<BillingAssetChildModel> childProductCode;
    @Schema(description = "총 리소스 양", example = "20")
    private Integer totalUnit;
    @Schema(description = "총 비용", example = "42.9326")
    private double totalCost;

}
