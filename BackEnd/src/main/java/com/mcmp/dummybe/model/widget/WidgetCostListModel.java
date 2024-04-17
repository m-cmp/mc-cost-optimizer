package com.mcmp.dummybe.model.widget;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WidgetCostListModel {
    private BigDecimal cost;
    private String item;
    private String itemAlias;
    private String vendor;
    private Boolean isOthers;
    private Integer order;
    private Boolean serviceGroup;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String date; // 측정 날짜
}
