package com.mcmp.dummybe.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardDataWidgetValues {
    private String widgetType;
    private List<String> filter;
    private List<String> dateType;
    private List<String> chartType;
    private List<String> scale;
    private Object timeFrame;
    private List<String> viewBy;
    private List<Integer> threshold;

    public DashboardDataWidgetValues(){

    }
    public void init() {
        this.widgetType = null;
        this.filter = null;
        this.dateType = null;
        this.chartType = null;
        this.scale = null;
        this.timeFrame = null;
        this.viewBy = null;
    }

}
