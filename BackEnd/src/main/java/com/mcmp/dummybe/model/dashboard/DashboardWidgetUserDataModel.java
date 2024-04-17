package com.mcmp.dummybe.model.dashboard;

import lombok.Data;

@Data
public class DashboardWidgetUserDataModel {
    private Integer dashboardIndex;
    private Integer widgetIndex;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private String widgetType;
    private String viewBy;
    private String dateType;
    private String timeFrame;
    private String chartType;
    private String scale;
    private String filter;
    private String selectedAccount;
    private String useYn;
    private String selectedVendorsByWidget;
    private Boolean isAbnormalNotiOn;

}
