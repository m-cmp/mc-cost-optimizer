package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.azure.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.azure.vm.rightsizer.mapper.AlarmHistoryMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
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

    private final AzureCredentialProperties azureCredentialProperties;
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
                    .resourceType("Virtual Machine")
                    .occureDt(new Timestamp(System.currentTimeMillis()))
                    .accountId(azureCredentialProperties.getSubscriptionId())
                    // Caution(주의), Warning(경고), Critical(긴급)
                    .urgency("Caution")
                    // Up(상향), Down(하향), Unused(미사용), Modernize(최신화)
                    .plan("Up")
                    .note("인스턴스(" + recommendVmTypeDto.getVmId() + ")를 기존 타입 : "
                            + recommendVmTypeDto.getCurrentType() + "에서 "
                            + recommendVmTypeDto.getRecommendType() + "Sizing 으로 변경하는 것을 추천드립니다.")
                    .occureDate(new Timestamp(System.currentTimeMillis()))
                    .cspType("AZURE")
                    .projectCd("ns01")
                    .build();
            alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
            // AlarmService에서 현재 DB history가 insert 되지 않아 넣은 코드.
            alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
            log.info("Saved {} Azure Vm Size Up Alarm History to database", recommendVmTypeDto.getVmId());
        }
    }
}
