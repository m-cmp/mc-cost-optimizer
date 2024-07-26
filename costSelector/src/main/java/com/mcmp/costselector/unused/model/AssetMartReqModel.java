package com.mcmp.costselector.unused.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AssetMartReqModel {
    private LocalDate cur_date;
    private double setting_value;
    private int setting_period;
    private String resource_id;
    private String metric_type;
}
