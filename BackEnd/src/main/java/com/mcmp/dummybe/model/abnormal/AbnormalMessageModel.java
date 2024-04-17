package com.mcmp.dummybe.model.abnormal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AbnormalMessageModel {
    private List<String> item;
    private BigDecimal cost;
    private BigDecimal rate;
    private String vendor;
    private Integer count;
    private String currency;
    private String timeFrame;
    private String viewBy;
}
