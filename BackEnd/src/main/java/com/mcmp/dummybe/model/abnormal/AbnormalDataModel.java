package com.mcmp.dummybe.model.abnormal;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class AbnormalDataModel {
    private String siteId;
    private String companyId;
    private String userId;
    private String userEmail;
    private Integer dashboardIndex;
    private Integer widgetIndex;
    private String level;
    private String detcBy;
    private String vendor;
    private String analDt;
    private BigDecimal cost;
    private BigDecimal rate;
    private String sensitivity;
    private String detcDt;
    private String alarmYn;
    private String hideYn;
    private String impotYn;
    private String creaDt;
    private BigDecimal minAlert;
    private BigDecimal maxAlert;
    private String widgetTitle;
    private String dashboardTitle;
    private Boolean savedHistory;
    private Object message;
}