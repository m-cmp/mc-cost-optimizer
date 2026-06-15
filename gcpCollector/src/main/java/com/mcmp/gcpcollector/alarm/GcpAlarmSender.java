package com.mcmp.gcpcollector.alarm;

import com.mcmp.gcpcollector.client.AlarmServiceClient;
import com.mcmp.gcpcollector.dto.AlarmHistoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GcpAlarmSender {

    private static final String CSP_TYPE = "GCP";
    private final AlarmServiceClient alarmServiceClient;

    /**
     * GCP 공통 알람 발송.
     * 호출부는 이벤트별 고유 필드만 채운 builder 를 넘기고,
     * cspType / alarmType / occureDt 는 이 메서드가 채운다.
     */
    public void send(AlarmHistoryDto.AlarmHistoryDtoBuilder builder) {
        AlarmHistoryDto dto = builder
                .alarmType(List.of("mail", "slack"))
                .cspType(CSP_TYPE)
                .occureDt(new Timestamp(System.currentTimeMillis()))
                .build();
        try {
            alarmServiceClient.sendOptiAlarmMail(dto);
        } catch (Exception e) {
            log.warn("알람 발송 실패 - eventType: {}, resourceId: {}, cause: {}",
                    dto.getEventType(), dto.getResourceId(), e.getMessage());
        }
    }
}
