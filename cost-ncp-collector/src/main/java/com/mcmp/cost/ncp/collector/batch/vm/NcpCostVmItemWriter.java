package com.mcmp.cost.ncp.collector.batch.vm;

import com.mcmp.cost.ncp.collector.client.AlarmServiceClient;
import com.mcmp.cost.ncp.collector.dto.AlarmHistoryDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import com.mcmp.cost.ncp.collector.mapper.AlarmHistoryMapper;
import com.mcmp.cost.ncp.collector.mapper.NcpCostVmDailyMapper;
import com.mcmp.cost.ncp.collector.service.BudgetMonthlyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class NcpCostVmItemWriter implements ItemWriter<List<NcpCostVmMonth>> {

    private final NcpCostVmDailyMapper ncpCostVmDailyMapper;
    private final AlarmHistoryMapper alarmHistoryMapper;
    private final AlarmServiceClient alarmServiceClient;
    private final BudgetMonthlyService budgetMonthlyService;

    @Override
    public void write(Chunk<? extends List<NcpCostVmMonth>> chunk) throws Exception {
        Double demandAmount = 0.0;
        String memberNo = "";
        for (List<NcpCostVmMonth> ncpCostVmMonthList : chunk) {
            if (ncpCostVmMonthList != null && !ncpCostVmMonthList.isEmpty()) {
                int totalInserted = 0;
                memberNo = ncpCostVmMonthList.getFirst().getMemberNo();
                for (NcpCostVmMonth ncpCostVmMonth : ncpCostVmMonthList) {
                    // writeDate를 LocalDate로 변환 (날짜 부분만 추출)
                    LocalDate targetDate = ncpCostVmMonth.getWriteDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // 일일 데이터 적재
                    int inserted = ncpCostVmDailyMapper.insertDailyCost(
                            ncpCostVmMonth.getInstanceNo(),
                            targetDate
                            // LocalDate.of(2025,9,30)
                    );
                    demandAmount += ncpCostVmMonth.getDemandAmount();
                    totalInserted += inserted;
                }
                log.info("Saved {} Ncp cost daily records to database", totalInserted);
            }

            // 예산 초과 체크 및 메일 발송
            BigDecimal usageRate = budgetMonthlyService.getCalculateBudgetRate(demandAmount);
            AlarmHistoryDto alarmHistoryDto = createBudgetAlarmDto(usageRate, memberNo);
            if (alarmHistoryDto != null) {
                sendEmail(alarmHistoryDto);
            }
        }
    }

    private AlarmHistoryDto createBudgetAlarmDto(BigDecimal usageRate, String memberNo) {
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
                .resourceId(memberNo)
                .resourceType("Virtual Machine")
                .occureDt(new Timestamp(System.currentTimeMillis()))
                .accountId(memberNo)
                .urgency(urgency)
                // 기존에 없던 plan, 예산을 위해 새롭게 작성.
                .plan("Adjustment")
                .note(note)
                .occureDate(new Timestamp(System.currentTimeMillis()))
                .cspType("NCP")
                .projectCd("ns01")
                .build();
    }

    private void sendEmail(AlarmHistoryDto alarmHistoryDto) {
        alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
        // AlarmService에서 현재 DB history가 insert 되지 않아 넣은 코드.
        alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
    }
}
