package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.client.AlarmServiceClient;
import com.mcmp.ncp.vm.rightsizer.dto.AlarmHistoryDto;
import com.mcmp.ncp.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.ncp.vm.rightsizer.mapper.AlarmHistoryMapper;
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

    private final AlarmHistoryMapper alarmHistoryMapper;
    private final DailyAbnormalByProductMapper dailyAbnormalByProductMapper;
    private final AlarmServiceClient alarmServiceClient;

    @Override
    public void write(Chunk<? extends AnomalyDto> chunk) throws Exception {
        for (AnomalyDto anomalyDto : chunk) {
            if (anomalyDto.getAbnormalRating() == null) {
                return;
            }

            AlarmHistoryDto alarmHistoryDto = AlarmHistoryDto.builder()
                    .alarmType(List.of("mail"))
                    // Abnormal (비정상), Resize(사이즈 변경), Unused(미사용)
                    .eventType("Abnormal")
                    .resourceId(anomalyDto.getVmId())
                    .resourceType(anomalyDto.getProductCd())
                    .occureDt(new Timestamp(System.currentTimeMillis()))
                    .accountId(anomalyDto.getMemberNo())
                    // Caution(주의), Warning(경고), Critical(긴급)
                    .urgency(anomalyDto.getAbnormalRating())
                    .plan(anomalyDto.getAbnormalRating())
                    .note("")
                    .occureDate(new Timestamp(System.currentTimeMillis()))
                    .cspType("NCP")
                    .projectCd("ns01")
                    .build();
            alarmServiceClient.sendOptiAlarmMail(alarmHistoryDto);
            // AlarmService에서 현재 DB history가 insert 되지 않아 넣은 코드.
            alarmHistoryMapper.insertAlarmHistory(alarmHistoryDto);
            // 이상비용 DB insert
            dailyAbnormalByProductMapper.insertDailyAbnormalByProduct(anomalyDto);
            log.info("Saved {} NCP Abnormal Alarm History to database", anomalyDto.getProjectCd());
        }
    }
}
