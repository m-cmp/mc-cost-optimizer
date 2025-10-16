package com.mcmp.costbe.budget.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "예산 응답 모델")
public class BudgetResModel {

    @Schema(description = "CSP 명", example = "AWS")
    private String csp;

    @Schema(description = "연도", example = "2025")
    private int year;

    @Schema(description = "월", example = "3")
    private int month;

    @Schema(description = "예산 금액", example = "400.00")
    private double budget;

    @Schema(description = "통화 단위", example = "USD")
    private String currency;
}
