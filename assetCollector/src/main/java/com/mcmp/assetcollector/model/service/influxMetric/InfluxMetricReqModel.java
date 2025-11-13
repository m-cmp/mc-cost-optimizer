package com.mcmp.assetcollector.model.service.influxMetric;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class InfluxMetricReqModel {
    private String measurement;
    private String range;
    private String group_time;
    private List<String> group_by;
    private int limit;
    private List<InfluxMetricField> fields;
    private List<InfluxMetricCondition> conditions;

    public InfluxMetricReqModel defaultValue(){
        this.measurement = "cpu";
        this.range = "1h";
        this.group_time = "12m";
        this.group_by = Arrays.asList("vm_id");
        this.limit = 0;

        return this;
    }
}
