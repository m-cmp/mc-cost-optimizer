package com.mcmp.slack_demo.common;

import com.mcmp.slack_demo.common.model.CommonResultModel;
import com.mcmp.slack_demo.common.model.costOpti.CostOptiAlarmReqModel;
import com.mcmp.slack_demo.mail.model.MailMessage;
import com.mcmp.slack_demo.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/alert")
public class CommonController {

    @Autowired
    private MailService mailService;

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
                        break;
                }
            }

        } catch (Exception e){
            result.setError(400, "Send sendOptiAlarmMail Fail");
        }
        return ResponseEntity.ok(result);
    }
}
