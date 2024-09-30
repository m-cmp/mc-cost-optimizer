package com.mcmp.assetcollector.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RSRCAssetUsageModel {
    private RSRCAssetUsageDataModel data;
    private String rsCode;
    private String rsMsg;
    private String errorMessage;
}
