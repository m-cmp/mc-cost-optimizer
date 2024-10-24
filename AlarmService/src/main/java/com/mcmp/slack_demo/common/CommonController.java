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
        try{
            for (String alarmType: reqModel.getAlarm_type()) {
                switch (alarmType){
                    case "mail":
                        mailService.sendEmail(reqModel, null);
                        break;
                    case "slack":
                        slackService.sendSlack(reqModel);
                        break;
                }
            }

        } catch (Exception e){
            result.setError(400, "Send sendOptiAlarmMail Fail");
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
