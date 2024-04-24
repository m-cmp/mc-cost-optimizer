package com.mcmp.slack_demo.mail.config;

import com.mcmp.slack_demo.mail.dao.MailingDao;
import com.mcmp.slack_demo.mail.model.MailingInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    public String host;

    @Value("${spring.mail.port}")
    public int port;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    public boolean auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    public boolean starttlsEnable;

    @Autowired
    private MailingDao mailingDao;

    private String userName;

    private String userPassword;


    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        if(userName != null && userPassword != null && !userName.isEmpty() && !userPassword.isEmpty()){
            mailSender.setUsername(this.userName);
            mailSender.setPassword(this.userPassword);
        }else {
            throw new RuntimeException("G-Mail 정보 부족");
        }

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.debug", "true");

        return mailSender;
    }

    public void setEmailInfo(MailingInfoModel model){
        this.userName = model.getUsername();
        this.userPassword = model.getPassword();
    }

    @Bean
    public MailConfig MailConfig(){
        MailingInfoModel mailInfo = mailingDao.getMailingInfo();
        if(mailInfo != null){
            this.userName = mailInfo.getUsername();
            this.userPassword = mailInfo.getPassword();
        }

        return new MailConfig();
    }

}
