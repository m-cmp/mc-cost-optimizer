package com.mcmp.slack_demo.mail.model;

import com.mcmp.slack_demo.common.model.costOpti.CostOptiAlarmReqModel;
import lombok.Data;

import java.util.List;

@Data
public class SendMailFormModel extends CostOptiAlarmReqModel {
    private List<String> to;
    private String subject;
    private String message;
    private String alarm_impl;
}
