package com.processor.costprocessor.model.unused;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyAssetAmountModel {
    private LocalDateTime creat_dt;
    private String metric_type;
    private String resource_id;
    private double metric_avg_amount;
    private LocalDate collect_dt;
}
