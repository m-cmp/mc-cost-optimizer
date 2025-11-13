package com.mcmp.costbe.budget.service;

import com.mcmp.costbe.budget.dao.BudgetDao;
import com.mcmp.costbe.budget.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetDao budgetDao;

    // CSP별 자동 통화 매핑 (DB 저장용)
    private static final Map<String, String> CSP_CURRENCY_MAP = Map.of(
            "AWS", "USD",
            "Azure", "KRW",  // Azure는 KRW로 수집/저장됨
            "NCP", "KRW"
    );

    public List<Integer> getAvailableYears() {
        return budgetDao.selectDistinctYears();
    }

    public List<BudgetResModel> getBudgetsByYear(int year, String projectId) {
        Map<String, Object> params = new HashMap<>();
        params.put("year", year);
        params.put("projectId", projectId);

        List<BudgetItemModel> list = budgetDao.selectBudgetByYear(params);
        List<BudgetResModel> result = new ArrayList<>();

        for (BudgetItemModel item : list) {
            double budgetValue = item.getBudget();

            // Azure와 NCP는 KRW로 저장되어 있으므로 USD로 변환하여 반환
            if ("Azure".equals(item.getCsp()) || "NCP".equals(item.getCsp())) {
                budgetValue = budgetValue / 1400.0;
            }

            result.add(BudgetResModel.builder()
                    .csp(item.getCsp())
                    .year(item.getYear())
                    .month(item.getMonth())
                    .budget(budgetValue)
                    .currency("USD")  // 프론트엔드는 모두 USD로 표시
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

            // DAO에 단일 항목 전달 (Azure/NCP는 프론트에서 KRW로 들어온 값 그대로 저장)
            budgetDao.upsertBudget(item);

            double responseBudget = item.getBudget();
            // 응답은 USD로 변환 (조회 API와 일관성 유지)
            if ("Azure".equals(item.getCsp()) || "NCP".equals(item.getCsp())) {
                responseBudget = responseBudget / 1400.0;
            }

            result.add(BudgetResModel.builder()
                    .csp(item.getCsp())
                    .year(item.getYear())
                    .month(item.getMonth())
                    .budget(responseBudget)
                    .currency("USD")
                    .build());
        }

        return result;
    }

    /**
     * 연도별 예산 vs 실제 사용 비교 조회
     */
    public BudgetComparisonResModel getBudgetComparison(int year, String projectId) {
        Map<String, Object> params = new HashMap<>();
        params.put("year", year);
        params.put("projectId", projectId);

        // 1. 예산 데이터 조회
        List<BudgetItemModel> budgetList = budgetDao.selectBudgetByYear(params);
        Map<String, Map<String, Double>> budgetMap = budgetList.stream()
                .collect(Collectors.groupingBy(
                        item -> String.format("%04d%02d", item.getYear(), item.getMonth()),
                        Collectors.toMap(
                                BudgetItemModel::getCsp,
                                BudgetItemModel::getBudget
                        )
                ));

        // 2. 실제 사용 데이터 조회
        List<ActualUsageItemModel> actualList = budgetDao.selectActualUsageByYear(params);
        Map<String, Map<String, Double>> actualMap = actualList.stream()
                .collect(Collectors.groupingBy(
                        ActualUsageItemModel::getYearMonth,
                        Collectors.toMap(
                                ActualUsageItemModel::getCsp,
                                ActualUsageItemModel::getBill
                        )
                ));

        // 3. 1~12월 데이터 생성
        List<MonthlyComparisonModel> months = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            String yearMonth = String.format("%04d%02d", year, month);

            // 예산 CSP별 매핑 (Azure와 NCP는 KRW이므로 USD로 변환)
            Map<String, Double> budgetForMonth = budgetMap.getOrDefault(yearMonth, new HashMap<>());
            double budgetAWS = budgetForMonth.getOrDefault("AWS", 0.0);
            double budgetNCP = budgetForMonth.getOrDefault("NCP", 0.0) / 1400.0;   // KRW -> USD
            double budgetAzure = budgetForMonth.getOrDefault("Azure", 0.0) / 1400.0; // KRW -> USD

            CspAmountModel budgetAmount = CspAmountModel.builder()
                    .total(budgetAWS + budgetNCP + budgetAzure)
                    .AWS(budgetAWS)
                    .NCP(budgetNCP)
                    .Azure(budgetAzure)
                    .build();

            // 실제 사용 CSP별 매핑
            Map<String, Double> actualForMonth = actualMap.getOrDefault(yearMonth, new HashMap<>());
            double actualAWS = actualForMonth.getOrDefault("AWS", 0.0);
            double actualNCP = actualForMonth.getOrDefault("NCP", 0.0);
            double actualAzure = actualForMonth.getOrDefault("Azure", 0.0);

            CspAmountModel actualAmount = CspAmountModel.builder()
                    .total(actualAWS + actualNCP + actualAzure)
                    .AWS(actualAWS)
                    .NCP(actualNCP)
                    .Azure(actualAzure)
                    .build();

            // 월별 비교 모델 생성
            months.add(MonthlyComparisonModel.builder()
                    .month(month)
                    .yearMonth(yearMonth)
                    .budget(budgetAmount)
                    .actual(actualAmount)
                    .build());
        }

        // 4. 최종 응답 모델 반환
        return BudgetComparisonResModel.builder()
                .year(year)
                .months(months)
                .build();
    }
}
