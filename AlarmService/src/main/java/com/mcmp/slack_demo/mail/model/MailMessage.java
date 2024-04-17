package com.mcmp.slack_demo.mail.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MailMessage {
    private List<String> to;
    private String subject;
    private String message;

}
