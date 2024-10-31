package com.mcmp.costselector.util.service;

import com.mcmp.costselector.model.util.AlarmReqModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AlarmService {

    @Value("${opti.alarm.url}")
    private String alarmURL;

    public void sendAlarm(AlarmReqModel req){
        try{
            String apiUrl = String.format("%s/api/costopti/alert/sendOptiAlarmMail", alarmURL);
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("alarm_type", List.of("mail", "slack"));
            body.put("event_type", req.getEvent_type());
            body.put("resource_id", req.getResource_id());
            body.put("resource_type", req.getResource_type());
            body.put("csp_type", req.getCsp_type());
            body.put("account_id", req.getAccount_id());
            body.put("urgency", req.getUrgency());
            body.put("plan", req.getPlan());
            body.put("note", req.getNote());
            body.put("project_cd", req.getProject_cd());
            HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

            restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException clientError) {
            String cleintErrorMsg = clientError.getMessage();
            log.error("FAIL TO SEND OPTI ALARM : " + cleintErrorMsg);
        }
    }
}
