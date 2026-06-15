package com.mcmp.costbe.budget.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "월별 예산 항목 모델")
public class BudgetItemModel {

    @Schema(description = "CSP 명", example = "AWS", required = true)
    private String csp;

    @Schema(description = "연도", example = "2025", required = true)
    private int year;

    @Schema(description = "월", example = "1", required = true)
    private int month;

    @Schema(description = "프로젝트 코드", example = "project-001", required = true)
    private String projectId;

    @Schema(description = "예산 금액", example = "400.00", required = true)
    private double budget;

    @Schema(description = "통화 단위 (자동 설정)", example = "USD")
    private String currency;
}
