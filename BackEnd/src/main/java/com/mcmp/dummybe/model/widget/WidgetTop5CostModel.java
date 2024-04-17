package com.mcmp.dummybe.model.widget;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetTop5CostModel {
    private BigDecimal cost;
    private String item;
    private String itemAlias;
    private String vendor;
    private Boolean isOthers;

    @JsonProperty(value = "numberOfOthers", required = false)
    private Integer numberOfOthers;
    private Integer order;
    private Boolean serviceGroup;
}
