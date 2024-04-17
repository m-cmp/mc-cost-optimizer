package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.util.List;

@Data
public class WidgetTop5DataModel {
    private List<WidgetTop5CostModel> cost;
    private String latestSummarizedBillDate;
}
