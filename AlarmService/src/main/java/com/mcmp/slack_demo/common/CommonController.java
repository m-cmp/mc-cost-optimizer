package com.mcmp.slack_demo.common;

import com.mcmp.slack_demo.common.model.CommonResultModel;
import com.mcmp.slack_demo.common.model.costOpti.CostOptiAlarmReqModel;
import com.mcmp.slack_demo.mail.service.MailService;
import com.mcmp.slack_demo.slack.api_client.SlackACService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/costopti/alert")
public class CommonController {

    @Autowired
    private MailService mailService;

    @Autowired
    private SlackACService slackService;

    @PostMapping("/sendOptiAlarmMail")
    public ResponseEntity<CommonResultModel> sendOptiAlarmMail(@RequestBody CostOptiAlarmReqModel reqModel){
        CommonResultModel result = new CommonResultModel();
        boolean anySuccess = false;
        StringBuilder errorMessages = new StringBuilder();

        for (String alarmType: reqModel.getAlarm_type()) {
            try{
                switch (alarmType){
                    case "mail":
                        mailService.sendEmail(reqModel, null);
                        anySuccess = true;
                        break;
                    case "slack":
                        slackService.sendSlack(reqModel);
                        anySuccess = true;
                        break;
                }
            } catch (Exception e){
                // 개별 알림 타입에서 에러가 발생해도 다른 알림은 계속 시도
                String errorMsg = String.format("Failed to send %s alarm: %s", alarmType, e.getMessage());
                errorMessages.append(errorMsg).append("; ");
                e.printStackTrace();
            }
        }

        // 모든 알림이 실패한 경우에만 에러 처리
        if (!anySuccess && errorMessages.length() > 0) {
            result.setError(400, "All alarm sending failed: " + errorMessages.toString());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/healthcheck")
    public ResponseEntity getHealthCheck(){
        try{
            return ResponseEntity.ok().body("OK");
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Alarm Service is not running");
        }
    }
}
