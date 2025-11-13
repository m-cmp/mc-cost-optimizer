package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.ncp.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.ncp.vm.rightsizer.dto.UnusedDto;
import com.mcmp.ncp.vm.rightsizer.mapper.UnusedBatchRstMapper;
import com.mcmp.ncp.vm.rightsizer.properties.NcpCredentialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.List;

/**
 * Unused Instance 알림 발송 ItemWriter
 */
@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class UnusedVMListItemWriter implements ItemWriter<UnusedDto> {

    private final NcpCredentialProperties ncpCredentialProperties;
    private final AlarmServiceClient alarmServiceClient;
    private final UnusedBatchRstMapper unusedBatchRstMapper;

    @Override
    public void write(Chunk<? extends UnusedDto> chunk) throws Exception {
        for (UnusedDto unusedDto : chunk) {
            if (unusedDto.getUnusedRating() == null) {
                continue;  // 등급이 없는 경우 skip
            }

            // Unused 메시지 생성
            String note = String.format(
                "Instance (%s) has very low CPU utilization over the past 14 days (average: %.2f%%, maximum: %.2f%%). " +
                "This resource may be unused and requires verification.",
                unusedDto.getInstanceNo(),
                unusedDto.getAvgCpu14Days(),
                unusedDto.getMaxCpu14Days()
            );

            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                .alarmType(List.of("mail", "slack"))
                // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                .eventType("Unused")
                .resourceId(unusedDto.getInstanceNo())
                .resourceType("Server")
                .occureDt(new Timestamp(System.currentTimeMillis()))
                .accountId(unusedDto.getMemberNo())
                // Unused는 항상 Warning 등급
                .urgency("Warning")
                .plan("Warning")
                .note(note)
                .occureDate(new Timestamp(System.currentTimeMillis()))
                .cspType("NCP")
                .projectCd(unusedDto.getProjectCd())
                .build();

            // unused_batch_rst 테이블에 Unused 판정 기록 (Recommend Job에서 중복 알림 방지용)
            // 알림 전송 성공 여부와 관계없이 항상 기록
            try {
                unusedBatchRstMapper.insertUnusedBatchRst(
                    "NCP",
                    unusedDto.getMemberNo(),
                    unusedDto.getInstanceNo(),
                    "Unused"
                );
                log.debug("Recorded unused instance to unused_batch_rst: {}", unusedDto.getInstanceNo());
            } catch (Exception e) {
                log.error("Failed to record unused instance to unused_batch_rst: {} - {}",
                    unusedDto.getInstanceNo(), e.getMessage());
            }

            // 알림 발송 (AlarmService에서 DB 저장도 처리함)
            try {
                alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
                log.info("Sent unused alarm to AlarmService for Instance: {} (project: {}, avg: {}%, max: {}%)",
                    unusedDto.getInstanceNo(), unusedDto.getProjectCd(),
                    String.format("%.2f", unusedDto.getAvgCpu14Days()),
                    String.format("%.2f", unusedDto.getMaxCpu14Days()));
            } catch (Exception e) {
                log.error("Failed to send unused alarm to AlarmService for Instance: {} - {}",
                    unusedDto.getInstanceNo(), e.getMessage());
            }
        }
    }
}
