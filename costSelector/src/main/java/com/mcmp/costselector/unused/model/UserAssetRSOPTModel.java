package com.mcmp.costselector.unused.model;

import lombok.Builder;
import lombok.Data;

@Data
public class UserAssetRSOPTModel {

    private String csp_type;
    private String metric_type;
    private int regress_duration;
    private double criteria_value;
    private String resource_id;

}
