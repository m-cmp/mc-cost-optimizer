package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WidgetProductPortionItemsModel {
    private String familyCode;
    private BigDecimal cost;
    private String item;
    private String itemAlias;
    private String vendor;
    private Boolean isOthers;
    private Integer order;
    private Boolean serviceGroup;
}
