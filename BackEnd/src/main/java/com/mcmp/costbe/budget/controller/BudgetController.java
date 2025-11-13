package com.mcmp.costbe.budget.controller;

import com.mcmp.costbe.budget.model.*;
import com.mcmp.costbe.budget.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/costopti/be/budget")
@Tag(name = "Budget Management", description = "CSP별 연도별 예산 관리 API")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/years")
    @Operation(summary = "존재하는 연도 목록 조회")
    public List<Integer> getAvailableYears() {
        return budgetService.getAvailableYears();
    }

    @GetMapping("/{year}")
    @Operation(summary = "연도별 예산 조회")
    public List<BudgetResModel> getBudgetsByYear(
            @PathVariable int year,
            @RequestParam String projectId) {
        return budgetService.getBudgetsByYear(year, projectId);
    }

    @PostMapping("/save")
    @Operation(summary = "예산 일괄 저장/업데이트 (통화 자동 설정)")
    public List<BudgetResModel> upsertBudgets(@RequestBody BudgetReqModel req) {
        return budgetService.upsertBudgets(req);
    }

    @GetMapping("/comparison/{year}")
    @Operation(summary = "연도별 예산 vs 실제 사용 비교", description = "월별로 예산과 실제 사용 금액을 CSP별로 비교합니다.")
    public BudgetComparisonResModel getBudgetComparison(
            @PathVariable int year,
            @RequestParam String projectId) {
        return budgetService.getBudgetComparison(year, projectId);
    }
}
