package com.mcmp.dummybe.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWidget {
    private Integer dashboardIndex;
    private Integer index;
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
    private List<String> selectedVendorsByWidget;
    private Boolean isAbnormalNotiOn;
}
