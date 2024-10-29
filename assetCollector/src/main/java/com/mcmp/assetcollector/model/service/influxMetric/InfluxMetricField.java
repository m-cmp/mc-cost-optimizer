package com.mcmp.assetcollector.model.service.influxMetric;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfluxMetricField {
    private String function;
    private String field;
}
