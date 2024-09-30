package com.mcmp.assetcollector.model.batch;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class RSRCAssetComputeMetricModel {
    private String cspType;
    private String cspAccount;
    private String cspInstanceid;
    private Timestamp collectDt;
    private String metricType;
    private double metricAmount;
    private String resourceStatus;
    private String resourceSpotYn;
}
