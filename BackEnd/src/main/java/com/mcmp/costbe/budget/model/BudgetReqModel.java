package com.mcmp.costbe.budget.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "예산 설정 요청 모델")
public class BudgetReqModel {

    @Schema(description = "예산 항목 목록", required = true)
    private List<BudgetItemModel> budgets;
}
