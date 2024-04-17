package com.mcmp.dummybe.model.widget;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetTrendDailyCostModel {
    @JsonProperty(value = "data", required = false)
    private String date;
    private String dailyDate;
    private BigDecimal cost;
}
