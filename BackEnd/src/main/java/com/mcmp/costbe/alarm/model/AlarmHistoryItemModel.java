package com.mcmp.costbe.alarm.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmHistoryItemModel {

    private String event_type;
    private String resource_id;
    private String resource_type;
    private LocalDateTime occure_time;
    private String csp_type;
    private String account_id;
    private String urgency;
    private String plan;
    private String note;
    private String project_cd;

}
