package com.mcmp.collector.client;

import com.mcmp.collector.dto.AlarmHistoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/costopti/alert")
public interface AlarmServiceClient {

    @PostExchange("/sendOptiAlarmMail")
    ResponseEntity<String> sendOptiAlarmMail(@RequestBody AlarmHistoryDto alarmHistoryDto);
}
