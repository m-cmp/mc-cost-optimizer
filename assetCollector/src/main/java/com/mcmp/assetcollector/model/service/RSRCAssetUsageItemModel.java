package com.mcmp.assetcollector.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RSRCAssetUsageItemModel {
    private Instant timestamp;
    private String value;
}
