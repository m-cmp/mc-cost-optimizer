package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.ncp.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
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
public class RecommendVmListItemWriter implements ItemWriter<RecommendVmTypeDto> {

    private final AlarmServiceClient alarmServiceClient;

    private String buildNote(String plan, RecommendVmTypeDto dto) {
        String base = String.format(
            "Recommend %s sizing for instance (%s) from current type %s to %s.",
            plan, dto.getVmId(), dto.getCurrentType(), dto.getRecommendType());
        if ("Down".equals(plan) && dto.getUsd() != null && dto.getUsd() > 0) {
            base += String.format(" Estimated monthly savings: $%.2f.", dto.getUsd());
        }
        return base;
    }

    @Override
    public void write(Chunk<? extends RecommendVmTypeDto> chunk) throws Exception {
        for (RecommendVmTypeDto recommendVmTypeDto : chunk) {
            // 후보 쿼리(selectRecommendCandidates)가 csp_instanceid 조인으로 이미 구한 projectCd 사용
            // (member_no 계정 조회는 실데이터에서 ncp_cost.member_no != meta.csp_account 라 깨짐)
            String projectCd = recommendVmTypeDto.getProjectCd() != null
                    ? recommendVmTypeDto.getProjectCd()
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
                    .note(buildNote(plan, recommendVmTypeDto))
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
