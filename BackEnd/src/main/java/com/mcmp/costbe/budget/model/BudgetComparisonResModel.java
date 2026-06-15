package com.mcmp.costbe.budget.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "연도별 예산 vs 실제 사용 비교 응답 모델")
public class BudgetComparisonResModel {

    @Schema(description = "연도", example = "2025")
    private Integer year;

    @Schema(description = "월별 비교 데이터 (1~12월)")
    private List<MonthlyComparisonModel> months;
}
