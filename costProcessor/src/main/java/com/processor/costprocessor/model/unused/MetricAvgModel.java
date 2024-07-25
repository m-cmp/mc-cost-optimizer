package com.processor.costprocessor.model.unused;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MetricAvgModel {
    private LocalDateTime creat_dt;
    private String resource_id;
    private LocalDateTime collect_dt;
    private String metric_type;
    private Double metric_avg_amount;

}
