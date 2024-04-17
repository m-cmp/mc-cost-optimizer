package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WidgetAbnormalListModel {
    private String item;
    private String itemAlias;
    private BigDecimal currentCost;
    private BigDecimal lastCost;
    private BigDecimal increaseDecreaseRate;
    private String costChanges;
    private String vendor;
    private String alarmLevel;
    private String levelStates;
}
