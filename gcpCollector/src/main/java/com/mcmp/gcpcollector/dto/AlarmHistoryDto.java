package com.mcmp.gcpcollector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmHistoryDto {

    @JsonProperty("alarm_type")
    private List<String> alarmType;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("resource_id")
    private String resourceId;

    @JsonProperty("resource_type")
    private String resourceType;

    @JsonProperty("occure_dt")
    private Timestamp occureDt;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("urgency")
    private String urgency;

    @JsonProperty("plan")
    private String plan;

    @JsonProperty("note")
    private String note;

    @JsonProperty("csp_type")
    private String cspType;

    @JsonProperty("project_cd")
    private String projectCd;
}
