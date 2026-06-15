package com.mcmp.costbe.budget.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "월별 예산 vs 실제 사용 비교 모델")
public class MonthlyComparisonModel {

    @Schema(description = "월", example = "1")
    private Integer month;

    @Schema(description = "연도월", example = "202501")
    private String yearMonth;

    @Schema(description = "예산 정보 (CSP별)")
    private CspAmountModel budget;

    @Schema(description = "실제 사용 정보 (CSP별)")
    private CspAmountModel actual;
}
