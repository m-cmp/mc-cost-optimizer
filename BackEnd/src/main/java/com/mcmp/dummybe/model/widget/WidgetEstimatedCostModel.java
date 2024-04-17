package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WidgetEstimatedCostModel {
    private BigDecimal currentMonthCost;
    private BigDecimal currentMonthEstimatedCost;
    private BigDecimal lastMonthTotalCost;
    private Double increaseDecreaseRate;
    private String selectedVendor;
}
