package com.mcmp.cost.ncp.collector.service;

import com.mcmp.cost.ncp.collector.dto.BudgetMonthlyDto;
import java.math.BigDecimal;

public interface BudgetMonthlyService {

    /**
     * 이번달 예산.
     *
     * @return 이번달 예산.
     */
    BudgetMonthlyDto getCurrentMonthBudget();

    /**
     * 예산 사용률 계산 (사용한 금액 / 전체 예산 * 100)
     *
     * @param budgetAmount 이번달 사용 비용.
     * @return 예산 사용률 계산 (사용한 금액 / 전체 예산 * 100).
     */
    BigDecimal getCalculateBudgetRate(Double budgetAmount);
}
