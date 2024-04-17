package com.mcmp.slack_demo.mail;

import com.mcmp.slack_demo.Common.model.CommonResultModel;
import com.mcmp.slack_demo.mail.model.MailMessage;
import com.mcmp.slack_demo.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cost")
public class MailController {
    @Autowired
    private MailService mailService;

    @PostMapping("/sendAlertMail")
    public ResponseEntity<CommonResultModel> sendAlertMail(@RequestBody MailMessage mailMessage){
        CommonResultModel result = new CommonResultModel();
        try{
            mailService.sendEmail(mailMessage, "alert");
        } catch (Exception e){
            result.setError(400, "Send AlertEmail Fail");
        }
        return ResponseEntity.ok(result);
    }

}
