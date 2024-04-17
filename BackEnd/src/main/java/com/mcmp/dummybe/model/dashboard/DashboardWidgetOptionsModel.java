package com.mcmp.dummybe.model.dashboard;

import lombok.Data;

@Data
public class DashboardWidgetOptionsModel {
    private String widgetType;
    private String viewBy;
    private String dateType;
    private String timeFrame;
    private String chartType;
    private String scale;
    private String filter;
    private String threshold;
}
