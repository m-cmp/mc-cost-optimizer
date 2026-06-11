package com.mcmp.gcpcollector.service;

import com.mcmp.gcpcollector.alarm.GcpAlarmSender;
import com.mcmp.gcpcollector.dao.GcpBudgetDao;
import com.mcmp.gcpcollector.dto.AlarmHistoryDto;
import com.mcmp.gcpcollector.dto.GcpBudgetUsageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpBudgetCheckService {

    private static final String RESOURCE_TYPE = "GCP Resources";
    private static final int    THRESHOLD     = 80;

    private final GcpBudgetDao   gcpBudgetDao;
    private final GcpAlarmSender gcpAlarmSender;

    public void check() {
        log.info("GCP 예산 체크 시작");

        List<GcpBudgetUsageDto> exceeded = gcpBudgetDao.selectGcpBudgetExceededProjects();

        if (exceeded == null || exceeded.isEmpty()) {
            log.info("예산 {}% 초과 프로젝트 없음", THRESHOLD);
            return;
        }

        log.info("예산 {}% 초과 프로젝트: {}건", THRESHOLD, exceeded.size());

        int successCount = 0;
        for (GcpBudgetUsageDto usage : exceeded) {
            if (usage.getBudget() == null || usage.getBudget().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("예산 미설정 - 스킵 - projectCd: {}", usage.getProjectCd());
                continue;
            }
            try {
                sendBudgetAlarm(usage);
                successCount++;
            } catch (Exception e) {
                log.error("예산 알람 발송 실패 - projectCd: {}", usage.getProjectCd(), e);
            }
        }

        log.info("GCP 예산 체크 완료 - 알람 발송: {}건", successCount);
    }

    private void sendBudgetAlarm(GcpBudgetUsageDto usage) {
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

        gcpAlarmSender.send(AlarmHistoryDto.builder()
                .eventType("Budget")
                .resourceId(usage.getProjectCd())
                .resourceType(RESOURCE_TYPE)
                .accountId(usage.getAccountId())
                .urgency(urgency)
                .plan("Adjustment")
                .note(note)
                .projectCd(usage.getProjectCd()));

        log.info("예산 알람 발송 - project: {}, 사용률: {}%, urgency: {}",
                usage.getProjectCd(),
                usage.getUsageRate().setScale(2, RoundingMode.HALF_UP),
                urgency);
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
