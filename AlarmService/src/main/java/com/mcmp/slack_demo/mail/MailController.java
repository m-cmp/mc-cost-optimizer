package com.mcmp.slack_demo.mail;

import com.mcmp.slack_demo.common.model.CommonResultModel;
import com.mcmp.slack_demo.mail.dao.MailingDao;
import com.mcmp.slack_demo.mail.model.MailMessage;
import com.mcmp.slack_demo.mail.model.MailingInfoModel;
import com.mcmp.slack_demo.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/costopti/alert")
public class MailController {
    @Autowired
    private MailService mailService;

    @Autowired
    private JavaMailSender emailSender;

    @PostMapping("/sendAlertMail")
    public ResponseEntity<CommonResultModel> sendAlertMail(@RequestBody MailMessage mailMessage){
        CommonResultModel result = new CommonResultModel();
        try{
            ClassPathResource resource = new ClassPathResource("static/images/testmemo.txt");
            mailService.sendEmail(mailMessage, "alert", resource);
        } catch (Exception e){
            result.setError(400, "Send AlertEmail Fail");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/insertMailInfo")
    public ResponseEntity<CommonResultModel> insertMailingInfo(@RequestBody MailingInfoModel model){
        CommonResultModel result = new CommonResultModel();
        try{
            mailService.setMailInfo(model);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(400, "Insert AlertEmail Info Fail");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getMailInfo")
    public ResponseEntity<CommonResultModel> getMailingInfo(){
        CommonResultModel result = new CommonResultModel();
        try{
            MailingInfoModel data = mailService.getMailingInfo();
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(400, "Get AlertEmail Info Fail");
        }
        return ResponseEntity.ok(result);
    }

}
