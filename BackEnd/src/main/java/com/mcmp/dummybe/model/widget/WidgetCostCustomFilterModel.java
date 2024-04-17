package com.mcmp.dummybe.model.widget;

import lombok.Data;

@Data
public class WidgetCostCustomFilterModel {
    private String item;
    private String itemAlias;
    private String vendor;
    private Integer order;
    private Boolean serviceGroup;
}
