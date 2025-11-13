package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.azure.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.azure.vm.rightsizer.dto.UnusedVmDto;
import com.mcmp.azure.vm.rightsizer.mapper.UnusedBatchRstMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.List;

/**
 * Unused VM 알림 발송 ItemWriter
 */
@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class UnusedVmListItemWriter implements ItemWriter<UnusedVmDto> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AlarmServiceClient alarmServiceClient;
    private final UnusedBatchRstMapper unusedBatchRstMapper;

    @Override
    public void write(Chunk<? extends UnusedVmDto> chunk) throws Exception {
        for (UnusedVmDto unusedVm : chunk) {
            if (unusedVm.getUnusedRating() == null) {
                continue;  // 등급이 없는 경우 skip
            }

            // Unused 메시지 생성
            String note = String.format(
                "VM (%s) has very low CPU usage over the past 14 days (average: %.2f%%, max: %.2f%%). " +
                "This may indicate an unused resource that needs verification.",
                unusedVm.getVmId(),
                unusedVm.getAvgCpu14Days(),
                unusedVm.getMaxCpu14Days()
            );

            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                .alarmType(List.of("mail", "slack"))
                // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                .eventType("Unused")
                .resourceId(unusedVm.getVmId())
                .resourceType("Virtual Machine")
                .occureDt(new Timestamp(System.currentTimeMillis()))
                .accountId(azureCredentialProperties.getSubscriptionId())
                // Unused는 항상 Warning 등급
                .urgency("Warning")
                .plan("Warning")
                .note(note)
                .occureDate(new Timestamp(System.currentTimeMillis()))
                .cspType("AZURE")
                .projectCd(unusedVm.getProjectCd())
                .build();

            // unused_batch_rst 테이블에 Unused 판정 기록 (Recommend Job에서 중복 알림 방지용)
            // 알림 전송 성공 여부와 관계없이 항상 기록
            try {
                unusedBatchRstMapper.insertUnusedBatchRst(
                    "AZURE",
                    azureCredentialProperties.getSubscriptionId(),
                    unusedVm.getVmId(),
                    "Unused"
                );
                log.debug("Recorded unused VM to unused_batch_rst: {}", unusedVm.getVmId());
            } catch (Exception e) {
                log.error("Failed to record unused VM to unused_batch_rst: {} - {}",
                    unusedVm.getVmId(), e.getMessage());
            }

            // 알림 발송 (AlarmService에서 DB 저장도 처리함)
            try {
                alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
                log.info("Sent unused alarm to AlarmService for VM: {} (project: {}, avg: {}%, max: {}%)",
                    unusedVm.getVmId(), unusedVm.getProjectCd(),
                    String.format("%.2f", unusedVm.getAvgCpu14Days()),
                    String.format("%.2f", unusedVm.getMaxCpu14Days()));
            } catch (Exception e) {
                log.error("Failed to send unused alarm to AlarmService for VM: {} - {}",
                    unusedVm.getVmId(), e.getMessage());
            }
        }
    }
}
