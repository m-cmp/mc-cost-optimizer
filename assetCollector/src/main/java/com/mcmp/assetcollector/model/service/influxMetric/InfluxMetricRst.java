package com.mcmp.assetcollector.model.service.influxMetric;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfluxMetricRst {
    private List<InfluxMetricRstItem> data;
    private String rsCode;
    private String rsMsg;
    private String errorMessage;
}
