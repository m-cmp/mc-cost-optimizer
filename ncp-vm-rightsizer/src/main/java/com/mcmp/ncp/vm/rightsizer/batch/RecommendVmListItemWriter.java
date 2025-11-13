package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.ncp.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.ncp.vm.rightsizer.mapper.ServiceGroupMetaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class RecommendVmListItemWriter implements ItemWriter<RecommendVmTypeDto> {

    private final AlarmServiceClient alarmServiceClient;
    private final ServiceGroupMetaMapper serviceGroupMetaMapper;

    @Override
    public void write(Chunk<? extends RecommendVmTypeDto> chunk) throws Exception {
        for (RecommendVmTypeDto recommendVmTypeDto : chunk) {
            // servicegroup_meta에서 projectCd, workspaceCd 조회
            Map<String, String> meta = serviceGroupMetaMapper.selectProjectAndWorkspaceByMemberNo(
                    recommendVmTypeDto.getMemberNo()
            );

            String projectCd = (meta != null && meta.get("projectCd") != null)
                    ? meta.get("projectCd")
                    : "default";

            String plan = recommendVmTypeDto.getPlan(); // "UP" or "DOWN"

            // AlarmGuideGrid.vue 파일을 보고 TYPE을 임시로 작성한다.
            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                    .alarmType(List.of("mail", "slack"))
                    // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                    .eventType("Resize")
                    .resourceId(recommendVmTypeDto.getVmId())
                    .resourceType("Server(VPC)")
                    .occureDt(new Timestamp(System.currentTimeMillis()))
                    .accountId(recommendVmTypeDto.getMemberNo())
                    // Caution(주의), Warning(경고), Critical(긴급)
                    .urgency("Caution")
                    // Up(상향), Down(하향), Unused(미사용), Modernize(최신화)
                    .plan(plan)
                    .note(String.format(
                        "Recommend %s sizing for instance (%s) from current type %s to %s.",
                        plan,
                        recommendVmTypeDto.getVmId(),
                        recommendVmTypeDto.getCurrentType(),
                        recommendVmTypeDto.getRecommendType()
                    ))
                    .occureDate(new Timestamp(System.currentTimeMillis()))
                    .cspType("NCP")
                    .projectCd(projectCd)  // servicegroup_meta에서 조회한 값 사용
                    .build();
            // 알림 발송 (AlarmService에서 DB 저장도 처리함)
            try {
                alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
                log.info("Sent resize alarm to AlarmService for vmId: {}, plan: {}, projectCd: {}, currentType: {} -> recommendType: {}",
                    recommendVmTypeDto.getVmId(), plan, projectCd,
                    recommendVmTypeDto.getCurrentType(), recommendVmTypeDto.getRecommendType());
            } catch (Exception e) {
                log.error("Failed to send resize alarm to AlarmService for vmId: {}, plan: {}, error: {}",
                    recommendVmTypeDto.getVmId(), plan, e.getMessage());
            }
        }
    }
}
