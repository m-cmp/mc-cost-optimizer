package com.mcmp.costbe.budget.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "실제 사용 금액 조회 결과 모델")
public class ActualUsageItemModel {

    @Schema(description = "연도월", example = "202501")
    private String yearMonth;

    @Schema(description = "CSP 명", example = "AWS")
    private String csp;

    @Schema(description = "실제 사용 금액", example = "450.00")
    private Double bill;
}
