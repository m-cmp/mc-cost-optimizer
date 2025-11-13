package com.mcmp.collector.batch;

import com.mcmp.collector.client.AlarmServiceClient;
import com.mcmp.collector.dao.BudgetCheckDao;
import com.mcmp.collector.dto.AlarmHistoryDto;
import com.mcmp.collector.dto.BudgetUsageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AWS 프로젝트별 예산 체크 Tasklet
 * - 프로젝트별로 이번 달 실제 사용 금액 집계
 * - 예산과 비교하여 80% 이상 사용 시 알림 발송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BudgetCheckTasklet implements Tasklet {

    private final BudgetCheckDao budgetCheckDao;
    private final AlarmServiceClient alarmServiceClient;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("====================================");
        log.info("Starting AWS Budget Check Tasklet");
        log.info("====================================");

        // 현재 연월 계산 (YYYYMM 형식)
        String currentYearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        log.info("Checking budget for year-month: {}", currentYearMonth);
        log.info("Query will use table: tbl_table_billing_detail_{}", currentYearMonth);

        // 1. 예산 80% 이상 사용한 프로젝트 조회
        log.info("Executing budget query...");
        List<BudgetUsageDto> budgetUsages = budgetCheckDao.selectBudgetExceededProjects(currentYearMonth);
        log.info("Query executed. Result count: {}", (budgetUsages != null ? budgetUsages.size() : 0));

        if (budgetUsages == null || budgetUsages.isEmpty()) {
            log.info("No projects exceeding 80% budget usage found.");
            return RepeatStatus.FINISHED;
        }

        log.info("Found {} project(s) exceeding budget threshold", budgetUsages.size());

        // 2. 각 프로젝트에 대해 알림 발송
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

        return RepeatStatus.FINISHED;
    }

    /**
     * 예산 알림 발송
     */
    private void sendBudgetAlarm(BudgetUsageDto usage) {
        // 등급 결정
        String urgency = determineUrgency(usage.getUsageRate());

        // 알림 메시지 생성
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

        // 알림 발송 (AlarmService에서 DB 저장도 처리함)
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

    /**
     * 사용률에 따른 등급 결정
     */
    private String determineUrgency(BigDecimal usageRate) {
        if (usageRate.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return "Critical";
        } else if (usageRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "Caution";
        }
        return "Warning";
    }
}
