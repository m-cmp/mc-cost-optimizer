package com.mcmp.cost.azure.collector.controller;

import com.mcmp.cost.azure.collector.dto.AlarmHistoryDto;
import com.mcmp.cost.azure.collector.properties.AzureCredentialProperties;
import com.mcmp.cost.azure.collector.service.impl.BudgetMonthlyServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class HelloController {

    private final BudgetMonthlyServiceImpl budgetMonthlyServiceImpl;
    private final AzureCredentialProperties azureCredentialProperties;

    @GetMapping(value = "/azure/budget/test")
    public ResponseEntity<String> test1() {
        String subscriptionId = azureCredentialProperties.getSubscriptionId();
        Double budgetInserted = budgetMonthlyServiceImpl.getCurrentMonthPreTaxCost(subscriptionId);

        BigDecimal usageRate = budgetMonthlyServiceImpl.getCalculateBudgetRate(budgetInserted);

        AlarmHistoryDto alarmHistoryDto = createBudgetAlarmDto(usageRate, subscriptionId);
        log.info("알람 DTO: {}", alarmHistoryDto);
        return ResponseEntity.ok("테스트.");
    }

    private AlarmHistoryDto createBudgetAlarmDto(BigDecimal usageRate, String subscriptionId) {
        String urgency;
        String note;

        if (usageRate.compareTo(BigDecimal.valueOf(100)) >= 0) {
            // 예산 초과 - Critical
            urgency = "Critical";
            note = "예산이 초과되었습니다. (사용률: " + usageRate.setScale(2, RoundingMode.HALF_UP) + "%)";
            log.warn("예산이 초과되었습니다. 사용률: {}%", usageRate.setScale(2, RoundingMode.HALF_UP));
        } else if (usageRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
            // 예산 80% 이상 - Caution
            urgency = "Caution";
            note = "설정한 예산 비용에 근접하게 사용 중입니다. (사용률: " + usageRate.setScale(2, RoundingMode.HALF_UP) + "%)";
            log.warn("예산금액을 거의 다 썼습니다. 사용률: {}%", usageRate.setScale(2, RoundingMode.HALF_UP));
        } else {
            // 알람 불필요
            return null;
        }

        return AlarmHistoryDto.builder()
                .alarmType(List.of("mail"))
                .eventType("Budget")
                .resourceId(subscriptionId)
                .resourceType("Virtual Machine")
                .occureDt(new Timestamp(System.currentTimeMillis()))
                .accountId(subscriptionId)
                .urgency(urgency)
                .plan("Adjustment")
                .note(note)
                .occureDate(new Timestamp(System.currentTimeMillis()))
                .cspType("Azure")
                .projectCd("ns01")
                .build();
    }
}
