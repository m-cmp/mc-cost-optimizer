package com.mcmp.cost.azure.collector.batch.vm;

import com.mcmp.cost.azure.collector.client.AlarmServiceClient;
import com.mcmp.cost.azure.collector.dto.AlarmHistoryDto;
import com.mcmp.cost.azure.collector.entity.AzureCostVmDaily;
import com.mcmp.cost.azure.collector.mapper.AlarmHistoryMapper;
import com.mcmp.cost.azure.collector.properties.AzureCredentialProperties;
import com.mcmp.cost.azure.collector.repository.AzureCostVmDailyRepository;
import com.mcmp.cost.azure.collector.service.BudgetMonthlyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AzureCostVmItemWriter implements ItemWriter<List<AzureCostVmDaily>> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AzureCostVmDailyRepository azureCostVmDailyRepository;
    private final AlarmHistoryMapper alarmHistoryMapper;
    private final AlarmServiceClient alarmServiceClient;
    private final BudgetMonthlyService budgetMonthlyService;

    @Override
    public void write(Chunk<? extends List<AzureCostVmDaily>> chunk) throws Exception {
        // 당일 VM 비용 저장.
        for (List<AzureCostVmDaily> azureCostVmDailyList : chunk) {
            if (azureCostVmDailyList != null && !azureCostVmDailyList.isEmpty()) {
                azureCostVmDailyRepository.saveAll(azureCostVmDailyList);
                azureCostVmDailyRepository.flush();
                log.info("Saved {} Azure cost vm records to database", azureCostVmDailyList.size());
            }
        }

        // 예산 초과 체크 및 메일 발송
        String subscriptionId = azureCredentialProperties.getSubscriptionId();
        Double budgetInserted = budgetMonthlyService.getCurrentMonthPreTaxCost(subscriptionId);
        BigDecimal usageRate = budgetMonthlyService.getCalculateBudgetRate(budgetInserted);
        AlarmHistoryDto alarmHistoryDto = createBudgetAlarmDto(usageRate, subscriptionId);
        if (alarmHistoryDto != null) {
            sendEmail(alarmHistoryDto);
        }
    }

    private AlarmHistoryDto createBudgetAlarmDto(BigDecimal usageRate, String subscriptionId) {
        String urgency;
        String note;

        if (usageRate.compareTo(BigDecimal.valueOf(100)) >= 0) {
            // 예산 초과 - Critical
            urgency = "Critical";
            note = "예산이 초과되었습니다. (사용률: " + usageRate.setScale(2, RoundingMode.HALF_UP) + "%)";
        } else if (usageRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
            // 예산 80% 이상 - Caution
            urgency = "Caution";
            note = "설정한 예산 비용에 근접하게 사용 중입니다. (사용률: " + usageRate.setScale(2, RoundingMode.HALF_UP) + "%)";
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

    private void sendEmail(AlarmHistoryDto alarmHistoryDto) {
        alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
        // AlarmService에서 현재 DB history가 insert 되지 않아 넣은 코드.
        alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
    }
}
