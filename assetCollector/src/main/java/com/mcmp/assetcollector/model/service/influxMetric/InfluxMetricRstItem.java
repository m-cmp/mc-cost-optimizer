package com.mcmp.assetcollector.model.service.influxMetric;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfluxMetricRstItem {
    private String name;
    private List<String> columns;
    private Map<String, Object> tags;
    private List<List<Object>> values;
}
