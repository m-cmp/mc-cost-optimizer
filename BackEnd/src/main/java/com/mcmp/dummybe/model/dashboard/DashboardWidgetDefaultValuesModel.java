package com.mcmp.dummybe.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardWidgetDefaultValuesModel {
    private String widgetType;
    private String viewBy;
    private String dateType;
    private String timeFrame;
    private String chartType;
    private String scale;
    private String filter;
    private Integer threshold;
}
