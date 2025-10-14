package com.mcmp.costbe.budget.service;

import com.mcmp.costbe.budget.dao.BudgetDao;
import com.mcmp.costbe.budget.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetDao budgetDao;

    // CSP별 자동 통화 매핑
    private static final Map<String, String> CSP_CURRENCY_MAP = Map.of(
            "AWS", "USD",
            "Azure", "USD",
            "NCP", "KRW"
    );

    public List<Integer> getAvailableYears() {
        return budgetDao.selectDistinctYears();
    }

    public List<BudgetResModel> getBudgetsByYear(int year) {
        List<BudgetItemModel> list = budgetDao.selectBudgetByYear(year);
        List<BudgetResModel> result = new ArrayList<>();

        for (BudgetItemModel item : list) {
            result.add(BudgetResModel.builder()
                    .csp(item.getCsp())
                    .year(item.getYear())
                    .month(item.getMonth())
                    .budget(item.getBudget())
                    .currency(item.getCurrency())
                    .build());
        }
        return result;
    }

    @Transactional
    public List<BudgetResModel> upsertBudgets(BudgetReqModel req) {
        List<BudgetResModel> result = new ArrayList<>();

        for (BudgetItemModel item : req.getBudgets()) {
            String currency = CSP_CURRENCY_MAP.getOrDefault(item.getCsp(), "USD");
            item.setCurrency(currency);

            // DAO에 단일 항목 전달
            budgetDao.upsertBudget(item);

            result.add(BudgetResModel.builder()
                    .csp(item.getCsp())
                    .year(item.getYear())
                    .month(item.getMonth())
                    .budget(item.getBudget())
                    .currency(item.getCurrency())
                    .build());
        }

        return result;
    }
}
