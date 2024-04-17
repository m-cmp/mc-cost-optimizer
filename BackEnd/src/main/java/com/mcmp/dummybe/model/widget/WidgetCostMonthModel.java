package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WidgetCostMonthModel {
    private String currentMonthDate;
    private BigDecimal currentMonthCost;
    private String lastMonthDate;
    private BigDecimal lastMonthCost;
    private Double increaseDecreaseRate;
    private String selectedVendor;
}
