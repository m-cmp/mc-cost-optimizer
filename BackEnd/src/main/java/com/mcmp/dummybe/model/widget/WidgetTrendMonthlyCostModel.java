package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.util.List;

@Data
public class WidgetTrendMonthlyCostModel {
    private String date;
    private List<WidgetTrendDailyCostModel> monthlyCost;
}
