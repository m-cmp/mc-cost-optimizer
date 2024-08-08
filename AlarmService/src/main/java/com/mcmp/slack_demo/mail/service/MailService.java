package com.mcmp.slack_demo.mail.service;

import com.mcmp.slack_demo.common.model.costOpti.CostOptiAlarmReqModel;
import com.mcmp.slack_demo.common.service.CommonService;
import com.mcmp.slack_demo.mail.config.MailConfig;
import com.mcmp.slack_demo.mail.dao.MailingDao;
import com.mcmp.slack_demo.mail.model.MailMessage;

import com.mcmp.slack_demo.mail.model.MailingInfoModel;
import com.mcmp.slack_demo.mail.model.SendMailFormModel;
import jakarta.activation.FileDataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class MailService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private MailingDao mailingDao;

    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private CommonService commonService;

    public void sendEmail(CostOptiAlarmReqModel optiAlarmReqModel, ClassPathResource file){
        SendMailFormModel mailFormModel = new SendMailFormModel();
        BeanUtils.copyProperties(optiAlarmReqModel, mailFormModel);
        mailFormModel.setOccure_time(ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime());

        try {
            JavaMailSender emailSender = mailConfig.getJavaMailSender();
            MimeMessage mimeMessage = emailSender.createMimeMessage();

            // find mail receiver
            mailFormModel.setTo(getAlarmMailReceivers(optiAlarmReqModel.getResource_id(), optiAlarmReqModel.getAccount_id()));

            String mailMessage = "[MCMP-Notice] Cost Alarm occurred";
            switch (optiAlarmReqModel.getEvent_type()){
                case "Unused":
                    mailFormModel.setSubject("[MCMP-Notice] Cost Alarm occurred : Caution Unused Resources");
                    mailMessage = "MCMP Cost에서 미사용 자원 주의 알람이 발생했습니다." +
                            "<br><br>" +
                            "계정 : " + mailFormModel.getAccount_id() + "<br>" +
                            "리소스 ID : " + mailFormModel.getResource_id();
                    break;
                case "Abnormal":
                    mailFormModel.setSubject("[MCMP-Notice] Cost Alarm occurred : Warning Abnormal Cost");
                    mailMessage = "MCMP Cost에서 이상 비용 경고 알람이 발생했습니다." +
                            "<br><br>" +
                            "계정 : " + mailFormModel.getAccount_id() + "<br>" +
                            "리소스 ID : " + mailFormModel.getResource_id();
                    break;
                case "Resize":
                    mailFormModel.setSubject("[MCMP-Notice] Cost Alarm occurred : Advise Resizing Resources");
                    mailMessage = "MCMP Cost에서 자원 최적화 권고 알람이 발생했습니다." +
                            "<br><br>" +
                            "계정 : " + mailFormModel.getAccount_id() + "<br>" +
                            "리소스 ID : " + mailFormModel.getResource_id();
                    break;
            }

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(setToList(mailFormModel.getTo()));
            mimeMessageHelper.setSubject(mailFormModel.getSubject());
            mimeMessageHelper.setText(mailMessage, true);

            emailSender.send(mimeMessage);

            log.info("############Send OptiAlertEmail Success############");

        } catch (Exception e) {
            log.info("############Send AlertEmail Fail############");
            e.printStackTrace();
        }

        commonService.insertAlarmHistory(mailFormModel);

    }

    public String sendEmail(MailMessage mailMessage, String type, ClassPathResource file){
        try {
            JavaMailSender emailSender = mailConfig.getJavaMailSender();
            MimeMessage mimeMessage = emailSender.createMimeMessage();

            Context context = new Context();
            context.setVariable("username", "테스트유저");
            String html = templateEngine.process("WelcomeT.html", context);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(setToList(mailMessage.getTo()));
            mimeMessageHelper.setSubject(mailMessage.getSubject());
            mimeMessageHelper.setText(html, true);

            ClassPathResource resource = new ClassPathResource("static/images/mcmp-logo.png");
            mimeMessageHelper.addInline("logo_png", resource.getFile());

            if("invoice".equals(type)){
                mimeMessageHelper.addAttachment(MimeUtility.encodeText("test.txt", "UTF-8", "B"), file.getFile());
            }

            emailSender.send(mimeMessage);

            log.info("############Send AlertEmail Success############");
            return "Success";

        } catch (Exception e) {
            log.info("############Send AlertEmail Fail############");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String[] setToList(List<String> toList){
        return toList.toArray(new String[toList.size()]);
    }

    public String setMailInfo(MailingInfoModel model){
        try{
            mailingDao.insertMailingInfo(model);
            mailConfig.setEmailInfo(model);
            return "Success";
        }catch (Exception e){
            log.info("############Send SetEmailInfo Fail############");
            throw new RuntimeException(e);
        }
    }

    public MailingInfoModel getMailingInfo(){
        return mailingDao.getMailingInfo();
    }

    private List<String> getAlarmMailReceivers(String resource_id, String account_id){
        Map<String, String> param = Map.of(
                "resource_id", resource_id,
                "account_id", account_id
        );

        return commonService.getAlarmMailReceivers(param);

    }

}
