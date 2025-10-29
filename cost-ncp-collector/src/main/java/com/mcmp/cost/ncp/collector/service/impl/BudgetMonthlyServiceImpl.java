커미package com.mcmp.cost.ncp.collector.service.impl;

import com.mcmp.cost.ncp.collector.dto.BudgetMonthlyDto;
import com.mcmp.cost.ncp.collector.mapper.BudgetMonthlyMapper;
import com.mcmp.cost.ncp.collector.service.BudgetMonthlyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetMonthlyServiceImpl implements BudgetMonthlyService {

    private final BudgetMonthlyMapper budgetMonthlyMapper;

    @Override
    public BudgetMonthlyDto getCurrentMonthBudget() {
        return budgetMonthlyMapper.selectCurrentMonthBudget("NCP");
    }

    @Override
    public BigDecimal getCalculateBudgetRate(Double budgetAmount) {
        BudgetMonthlyDto budgetMonthlyDto = this.getCurrentMonthBudget();

        // 예산이 설정되지 않은 경우 null 반환 (알람 미발송)
        if (budgetMonthlyDto == null || budgetMonthlyDto.getBudget() == null) {
            log.info("예산이 설정되지 않아 예산 사용률 계산을 건너뜁니다.");
            return null;
        }

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
}
