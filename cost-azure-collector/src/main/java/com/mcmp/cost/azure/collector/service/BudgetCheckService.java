package com.mcmp.cost.azure.collector.service;

import com.mcmp.cost.azure.collector.client.AlarmServiceClient;
import com.mcmp.cost.azure.collector.dto.AlarmHistoryDto;
import com.mcmp.cost.azure.collector.dto.BudgetUsageDto;
import com.mcmp.cost.azure.collector.mapper.BudgetCheckMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

/**
 * Azure 프로젝트별 예산 체크
 * - 프로젝트별로 이번 달 실제 사용 금액 집계
 * - 예산과 비교하여 80% 이상 사용 시 알림 발송
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetCheckService {

    private final BudgetCheckMapper budgetCheckMapper;
    private final AlarmServiceClient alarmServiceClient;

    public void check() {
        log.info("====================================");
        log.info("Starting Azure Budget Check");
        log.info("====================================");

        List<BudgetUsageDto> budgetUsages = budgetCheckMapper.selectBudgetExceededProjects();

        if (budgetUsages == null || budgetUsages.isEmpty()) {
            log.info("No projects exceeding 80% budget usage found.");
            return;
        }

        log.info("Found {} project(s) exceeding budget threshold", budgetUsages.size());

        int successCount = 0;
        for (BudgetUsageDto usage : budgetUsages) {
            try {
                sendBudgetAlarm(usage);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to send budget alarm for project: {}, csp: {}",
                    usage.getProjectCd(), usage.getCspType(), e);
            }
        }

        log.info("====================================");
        log.info("Budget check completed. {} alarms sent successfully.", successCount);
        log.info("====================================");
    }

    private void sendBudgetAlarm(BudgetUsageDto usage) {
        String urgency = determineUrgency(usage.getUsageRate());

        String note = String.format(
            "Project '%s' budget usage is at %s%% for %s. (Used: %s %s / Budget: %s %s)",
            usage.getProjectCd(),
            usage.getUsageRate().setScale(2, RoundingMode.HALF_UP),
            usage.getCspType(),
            usage.getTotalCost().setScale(2, RoundingMode.HALF_UP),
            usage.getCurrency(),
            usage.getBudget().setScale(2, RoundingMode.HALF_UP),
            usage.getCurrency()
        );

        AlarmHistoryDto alarm = AlarmHistoryDto.builder()
            .alarmType(List.of("mail", "slack"))
            .eventType("Budget")
            .resourceId(usage.getProjectCd())
            .resourceType(usage.getCspType() + " Resources")
            .occureDt(new Timestamp(System.currentTimeMillis()))
            .accountId(usage.getAccountId())
            .urgency(urgency)
            .plan("Adjustment")
            .note(note)
            .occureDate(new Timestamp(System.currentTimeMillis()))
            .cspType(usage.getCspType())
            .projectCd(usage.getProjectCd())
            .build();

        try {
            alarmServiceClient.sendOptiAlarmMail(alarm);
            log.info("Budget alarm sent to AlarmService: project={}, csp={}, usage={}%, urgency={}",
                usage.getProjectCd(), usage.getCspType(),
                usage.getUsageRate().setScale(2, RoundingMode.HALF_UP), urgency);
        } catch (Exception e) {
            log.error("Failed to send budget alarm to AlarmService for project: {}, csp: {} - {}",
                usage.getProjectCd(), usage.getCspType(), e.getMessage());
        }
    }

    private String determineUrgency(BigDecimal usageRate) {
        if (usageRate.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return "Critical";
        } else if (usageRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "Caution";
        }
        return "Warning";
    }
}
