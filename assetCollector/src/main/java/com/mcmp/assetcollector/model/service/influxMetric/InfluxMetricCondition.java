package com.mcmp.assetcollector.model.service.influxMetric;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfluxMetricCondition {
    private String key;
    private String value;
}
