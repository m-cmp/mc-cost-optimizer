package com.mcmp.cost.ncp.collector.controller;

import com.mcmp.cost.ncp.collector.service.BudgetCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 배치 주기(Quartz cron)를 기다리지 않고 즉시 실행하기 위한 관리용 엔드포인트.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminBudgetController {

    private final BudgetCheckService budgetCheckService;

    @GetMapping("/budget/check")
    public ResponseEntity<String> checkBudget() {
        try {
            budgetCheckService.check();
            return ResponseEntity.ok("NCP Budget Check triggered successfully");
        } catch (Exception e) {
            log.error("NCP Budget Check 실행 실패", e);
            return ResponseEntity.internalServerError().body("Failed to trigger budget check: " + e.getMessage());
        }
    }
}
