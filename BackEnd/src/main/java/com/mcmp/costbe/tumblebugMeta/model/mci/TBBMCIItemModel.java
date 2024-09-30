package com.mcmp.costbe.tumblebugMeta.model.mci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TBBMCIItemModel {
    private String configureCloudAdaptiveNetwork;
    private String description;
    private String id;
    private String installMonAgent;
    private Map<String, String> label;
    private String name;
    private List<String> newVmList;
    private String placementAlgo;
    private String resourceType;
    private String status;
    private Object statusCount;
    private String systemLabel;
    private String systemMessage;
    private String targetAction;
    private String targetStatus;
    private String uid;
    private List<TbVmInfoModel> vm;
}
