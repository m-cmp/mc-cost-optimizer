package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.util.List;

@Data
public class WidgetTrendDataModel {
    private List<WidgetTrendMonthlyCostModel> trendCost;
    private String endDate;
}
