package com.mcmp.assetcollector.model.service.influxMetric;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public class InfluxMetricReqModel {
    private int influx_db_seq;
    private String measurement;
    private String range;
    private String group_time;
    private List<String> group_by;
    private int limit;
    private List<InfluxMetricField> fields;
    private List<InfluxMetricCondition> conditions;

    public InfluxMetricReqModel defaultValue(){
        this.influx_db_seq = 1;
        this.measurement = "cpu";
        this.range = "1h";
        this.group_time = "1h";
        this.group_by = Arrays.asList("uuid", "cpu");
        this.limit = 10;

        return this;
    }
}
