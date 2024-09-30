package com.mcmp.assetcollector.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RSRCAssetUsageDataModel {
    private String metricName;
    private String metricUnit;
    private List<RSRCAssetUsageItemModel> timestampValues;
}
