package com.mcmp.slack_demo.common.model.costOpti;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CostOptiAlarmModel {
    private String event_type;
    private String resource_id;
    private String resource_type;
    private LocalDateTime occure_time;
    private String account_id;
    private String urgency;
    private String note;

}
