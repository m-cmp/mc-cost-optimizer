package com.mcmp.azure.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
public class AlarmHistoryDto {

    @JsonProperty(value = "alarm_type")
    private List<String> alarmType;

    @JsonProperty(value = "event_type")
    private String eventType;

    @JsonProperty(value = "resource_id")
    private String resourceId;

    @JsonProperty(value = "resource_type")
    private String resourceType;

    @JsonProperty(value = "occure_dt")
    private Timestamp occureDt;

    @JsonProperty(value = "account_id")
    private String accountId;

    @JsonProperty(value = "urgency")
    private String urgency;

    @JsonProperty(value = "plan")
    private String plan;

    @JsonProperty(value = "note")
    private String note;

    @JsonProperty(value = "occure_date")
    private Timestamp occureDate;

    @JsonProperty(value = "csp_type")
    private String cspType;

    @JsonProperty(value = "project_cd")
    private String projectCd;

    @Builder
    public AlarmHistoryDto(List<String> alarmType, String eventType, String resourceId, String resourceType, Timestamp occureDt, String accountId, String urgency, String plan, String note, Timestamp occureDate, String cspType, String projectCd) {
        this.alarmType = alarmType;
        this.eventType = eventType;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.occureDt = occureDt;
        this.accountId = accountId;
        this.urgency = urgency;
        this.plan = plan;
        this.note = note;
        this.occureDate = occureDate;
        this.cspType = cspType;
        this.projectCd = projectCd;
    }
}
