package com.mcmp.cost.azure.collector.service.impl;

import com.mcmp.cost.azure.collector.dto.BudgetMonthlyDto;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.mapper.BudgetMonthlyMapper;
import com.mcmp.cost.azure.collector.properties.AzureCredentialProperties;
import com.mcmp.cost.azure.collector.repository.AzureCostVmDailyRepository;
import com.mcmp.cost.azure.collector.service.BudgetMonthlyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetMonthlyServiceImpl implements BudgetMonthlyService {

    private final AzureCredentialProperties azureCredentialProperties;
    private final BudgetMonthlyMapper budgetMonthlyMapper;
    private final AzureCostVmDailyRepository azureCostVmDailyRepository;

    @Override
    public BudgetMonthlyDto getCurrentMonthBudget() {
        return budgetMonthlyMapper.selectCurrentMonthBudget("Azure");
    }

    @Override
    public BigDecimal getCalculateBudgetRate(Double budgetAmount) {
        BudgetMonthlyDto budgetMonthlyDto = this.getCurrentMonthBudget();
        BigDecimal budget = budgetMonthlyDto.getBudget();
        BigDecimal demandAmountDecimal = BigDecimal.valueOf(budgetAmount);
        BigDecimal remainingBudget = budget.subtract(demandAmountDecimal);
        log.info("이번달 예산 금액: {}, 이번달 VM 총 사용 금액: {}, 남은 예산 금액: {}", budget, demandAmountDecimal, remainingBudget);

        // 남은 금액 = 이번달 예산 금액 - 사용 금액
        BigDecimal usedAmount = budget.subtract(remainingBudget);
        // 예산 사용률 계산 (사용한 금액 / 전체 예산 * 100)
        return budget.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.valueOf(100)
                : usedAmount.divide(budget, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Override
    public Double getCurrentMonthPreTaxCost(String subscriptionId) {
        List<AzureCostVmDaily> azureCostVmDailyList = azureCostVmDailyRepository
                .findBySubscriptionIdAndYearMonth(
                        subscriptionId,
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")));
        Double monthPreTaxCost = 0.0;
        for (AzureCostVmDaily azureCostVmDaily : azureCostVmDailyList) {
            monthPreTaxCost += azureCostVmDaily.getPreTaxCost();
        }

        return monthPreTaxCost;
    }
}
