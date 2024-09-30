package com.processor.costprocessor.model.util;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlarmReqModel {
    private String event_type;
    private String resource_id;
    private String resource_type;
    private String csp_type;
    private String account_id;
    private String urgency;
    private String plan;
    private String note;
    private String project_cd;
}
