package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.ncp.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.ncp.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.ncp.vm.rightsizer.mapper.DailyAbnormalByProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AnomalyVmListItemWriter implements ItemWriter<AnomalyDto> {

    private final DailyAbnormalByProductMapper dailyAbnormalByProductMapper;
    private final AlarmServiceClient alarmServiceClient;

    @Override
    public void write(Chunk<? extends AnomalyDto> chunk) throws Exception {
        for (AnomalyDto anomalyDto : chunk) {
            if (anomalyDto.getAbnormalRating() == null) {
                continue;  // 등급이 없는 경우 skip하고 다음 항목으로
            }

            // 등급별 메시지 생성
            String note = String.format(
                    "VM (%s) cost has increased by %.1f%% compared to last month's average (%.2f KRW).",
                    anomalyDto.getVmId(),
                    anomalyDto.getPercentagePoint(),
                    anomalyDto.getSubjectCost()
            );

            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                    .alarmType(List.of("mail", "slack"))
                    // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                    .eventType("Abnormal")
                    .resourceId(anomalyDto.getVmId())
                    .resourceType(anomalyDto.getProductCd())
                    .occureDt(new Timestamp(System.currentTimeMillis()))
                    .accountId(anomalyDto.getMemberNo())
                    // Caution(주의), Warning(경고), Critical(긴급)
                    .urgency(anomalyDto.getAbnormalRating())
                    .plan(anomalyDto.getAbnormalRating())
                    .note(note)
                    .occureDate(new Timestamp(System.currentTimeMillis()))
                    .cspType("NCP")
                    .projectCd(anomalyDto.getProjectCd())  // AnomalyDto에서 가져온 값 사용 (servicegroup_meta 조인)
                    .build();

            // 알림 발송 (AlarmService에서 DB 저장도 처리함)
            try {
                alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
                log.info("Sent abnormal alarm to AlarmService for VM: {} (project: {}, rating: {}, percentage: {}%)",
                    anomalyDto.getVmId(), anomalyDto.getProjectCd(), anomalyDto.getAbnormalRating(),
                    String.format("%.1f", anomalyDto.getPercentagePoint()));
            } catch (Exception e) {
                log.error("Failed to send abnormal alarm to AlarmService for VM: {} - {}",
                    anomalyDto.getVmId(), e.getMessage());
            }

            // 이상비용 DB insert
            dailyAbnormalByProductMapper.insertDailyAbnormalByProduct(anomalyDto);
        }
    }
}
