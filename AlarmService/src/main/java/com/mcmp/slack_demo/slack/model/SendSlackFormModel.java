package com.mcmp.slack_demo.slack.model;

import com.mcmp.slack_demo.common.model.costOpti.CostOptiAlarmReqModel;
import lombok.Data;

@Data
public class SendSlackFormModel extends CostOptiAlarmReqModel {
    private String title;
    private String message;
    private String alarm_impl;
}
