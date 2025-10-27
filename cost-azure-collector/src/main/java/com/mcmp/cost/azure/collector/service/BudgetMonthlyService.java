package com.mcmp.cost.azure.collector.service;

import com.mcmp.cost.azure.collector.dto.BudgetMonthlyDto;
import java.math.BigDecimal;

public interface BudgetMonthlyService {

    /**
     * 이번달 예산.
     *
     * @return 이번달 예산.
     */
    BudgetMonthlyDto getCurrentMonthBudget();

    /**
     * 예산 사용률 계산 (사용한 금액 / 전체 예산 * 100).
     *
     * @param budgetAmount 이번달 사용 비용.
     * @return 예산 사용률 계산 (사용한 금액 / 전체 예산 * 100)
     */
    BigDecimal getCalculateBudgetRate(Double budgetAmount);

    /**
     * 이번달 VM 총 사용 비용.
     *
     * @param subscriptionId 구독 아이디.
     * @return 이번달 VM 총 사용 비용.
     */
    Double getCurrentMonthPreTaxCost(String subscriptionId);
}
