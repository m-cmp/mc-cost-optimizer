package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.ncp.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.ncp.vm.rightsizer.mapper.AlarmHistoryMapper;
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

    private final AlarmHistoryMapper alarmHistoryMapper;
    private final AlarmServiceClient alarmServiceClient;

    @Override
    public void write(Chunk<? extends RecommendVmTypeDto> chunk) throws Exception {
        for (RecommendVmTypeDto recommendVmTypeDto : chunk) {
            // AlarmGuideGrid.vue 파일을 보고 TYPE을 임시로 작성한다.

            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                    .alarmType(List.of("mail"))
                    // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                    .eventType("Resize")
                    .resourceId(recommendVmTypeDto.getVmId())
                    .resourceType("Server(VPC)")
                    .occureDt(new Timestamp(System.currentTimeMillis()))
                    .accountId(recommendVmTypeDto.getMemberNo())
                    // Caution(주의), Warning(경고), Critical(긴급)
                    .urgency("Caution")
                    // Up(상향), Down(하향), Unused(미사용), Modernize(최신화)
                    .plan("Down")
                    .note("인스턴스(" + recommendVmTypeDto.getVmId() + ")를 기존 타입 : "
                            + recommendVmTypeDto.getCurrentType() + "에서 "
                            + recommendVmTypeDto.getRecommendType() + "Sizing 으로 변경하는 것을 추천드립니다.")
                    .occureDate(new Timestamp(System.currentTimeMillis()))
                    .cspType("NCP")
                    .projectCd("ns01")
                    .build();
            alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
            alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
            log.info("Saved {} Ncp Vm Size Down Alarm History to database", recommendVmTypeDto.getVmId());
        }
    }
}
