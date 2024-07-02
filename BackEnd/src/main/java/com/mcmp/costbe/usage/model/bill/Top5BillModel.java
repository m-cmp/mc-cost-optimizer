package com.mcmp.costbe.usage.model.bill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Top5BillModel {
    @Schema(description = "비용", example = "5.28")
    private double bill;
    @Schema(description = "리소스 이름", example = "AmazonEC2")
    private String resourceNm;
    @Schema(description = "csp 정보", example = "AWS")
    private String csp;
    @Schema(description = "그 외 여부", example = "false")
    private Boolean isOthers;
}
