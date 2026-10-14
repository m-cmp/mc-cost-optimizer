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
        mailFormModel.setOccure_time(ZonedDateTime.now().toLocalDateTime());
        mailFormModel.setAlarm_impl("mail");

        try {
            int checkDuplicateMail = commonService.getAlertDuplicate(mailFormModel);
            if (checkDuplicateMail >= 1) {
                log.info("############Send OptiAlertEmail Duplicate : {} - {} - {}############",
                        mailFormModel.getEvent_type(), mailFormModel.getResource_id(),
                        (mailFormModel.getAccount_id() != null ? mailFormModel.getAccount_id() : mailFormModel.getProject_cd()));
                return;
            }
        } catch (Exception e) {
            log.warn("중복 체크 실패 - 중복 아님으로 간주하고 진행 (resource_id: {}, cause: {})",
                    mailFormModel.getResource_id(), e.getMessage());
        }

        try {
            // find mail receiver
            List<String> receivers = getAlarmMailReceivers(optiAlarmReqModel);
            if (receivers == null || receivers.isEmpty()) {
                log.warn("수신자 없음 - 메일 발송 스킵 (resource_id: {}, project_cd: {})",
                        optiAlarmReqModel.getResource_id(), optiAlarmReqModel.getProject_cd());
                return;
            }
            mailFormModel.setTo(receivers);

            JavaMailSender emailSender = mailConfig.getJavaMailSender();
            MimeMessage mimeMessage = emailSender.createMimeMessage();

            String mailMessage = "[MCMP-Notice] Cost Alarm occurred";
            switch (optiAlarmReqModel.getEvent_type()){
                case "Unused":
                    mailFormModel.setSubject("[MCMP-Notice] Cost Alarm occurred : Caution Unused Resources");
                    mailMessage = "MCMP Cost has detected a caution-level unused resource alarm." +
                            "<br><br>" +
                            "CSP : " + mailFormModel.getCsp_type() + "<br>" +
                            "Resource ID : " + mailFormModel.getResource_id() + "<br>" +
                            "Resource Type : " + mailFormModel.getResource_type() + "<br>" +
                            "This resource is suspected to be unused.";
                    break;
                case "Abnormal":
                    mailFormModel.setSubject("[MCMP-Notice] Cost Alarm occurred : Warning Abnormal Cost");
                    mailMessage = "MCMP Cost has detected an abnormal cost warning alarm." +
                            "<br><br>" +
                            "CSP : " + mailFormModel.getCsp_type() + "<br>" +
                            "Product : " + mailFormModel.getResource_type() + "<br>" +
                            "Abnormal Cost Rating : " + mailFormModel.getPlan() + "<br>" +
                            "An abnormal cost has occurred. " + mailFormModel.getNote();
                    break;
                case "Resize":
                    mailFormModel.setSubject("[MCMP-Notice] Cost Alarm occurred : Advise Right Size Resources");
                    mailMessage = "MCMP Cost has detected a resource rightsizing recommendation alarm." +
                            "<br><br>" +
                            "CSP : " + mailFormModel.getCsp_type() + "<br>" +
                            "Resource ID : " + mailFormModel.getResource_id() + "<br>" +
                            "Resource Type : " + mailFormModel.getResource_type() + "<br>" +
                            "Recommended Plan : " + mailFormModel.getPlan() + "<br>" +
                            mailFormModel.getNote();
                    break;
                case "Budget":
                    String urgencyLevel = "Caution".equals(mailFormModel.getUrgency()) ? "Caution" : "Critical";
                    mailFormModel.setSubject("[MCMP-Notice] Cost Alarm occurred : " +
                            (urgencyLevel.equals("Critical") ? "Critical" : "Caution") + " Budget Usage");
                    mailMessage = "MCMP Cost has detected a " + urgencyLevel + "-level budget exceeded alarm." +
                            "<br><br>" +
                            "CSP : " + mailFormModel.getCsp_type() + "<br>" +
                            "Project : " + mailFormModel.getProject_cd() + "<br>" +
                            "Budget Usage Rating : " + mailFormModel.getUrgency() + "<br>" +
                            mailFormModel.getNote();
                    break;
                default:
                    log.warn("Unknown event_type: {}", optiAlarmReqModel.getEvent_type());
                    mailFormModel.setSubject("[MCMP-Notice] Cost Alarm occurred : Unknown Event");
                    mailMessage = "MCMP Cost has detected an alarm." +
                            "<br><br>" +
                            "Event Type : " + optiAlarmReqModel.getEvent_type() + "<br>" +
                            "CSP : " + mailFormModel.getCsp_type() + "<br>" +
                            mailFormModel.getNote();
                    break;
            }

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(setToList(mailFormModel.getTo()));
            mimeMessageHelper.setSubject(mailFormModel.getSubject());
            mimeMessageHelper.setText(mailMessage, true);

            emailSender.send(mimeMessage);

            log.info("############Send OptiAlertEmail Success############");

        } catch (Exception e) {
            log.warn("############Send AlertEmail Fail - resource_id: {}, cause: {}############",
                    optiAlarmReqModel.getResource_id(), e.getMessage());
        }

        try {
            commonService.insertAlarmHistory(mailFormModel);
        } catch (Exception e) {
            log.warn("알람 이력 저장 실패 - resource_id: {}, cause: {}",
                    optiAlarmReqModel.getResource_id(), e.getMessage());
        }

    }

    public String sendEmail(MailMessage mailMessage, String type, ClassPathResource file){
        try {
            JavaMailSender emailSender = mailConfig.getJavaMailSender();
            MimeMessage mimeMessage = emailSender.createMimeMessage();

            Context context = new Context();
            context.setVariable("username", "Test User");
            String html = templateEngine.process("WelcomeT.html", context);

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setTo(setToList(mailMessage.getTo()));
            mimeMessageHelper.setSubject(mailMessage.getSubject());
            mimeMessageHelper.setText(html, true);

            ClassPathResource resource = new ClassPathResource("static/images/mcmp-logo.png");
            mimeMessageHelper.addInline("logo_png", resource, "image/png");

//            if("invoice".equals(type)){
//                mimeMessageHelper.addAttachment(MimeUtility.encodeText("test.txt", "UTF-8", "B"), file.getFile());
//            }

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

    private List<String> getAlarmMailReceivers(CostOptiAlarmReqModel param){
        return commonService.getAlarmMailReceivers(param);
    }

}
