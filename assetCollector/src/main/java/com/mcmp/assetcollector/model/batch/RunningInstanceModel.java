package com.mcmp.assetcollector.model.batch;

import lombok.Data;

@Data
public class RunningInstanceModel {
    private String cspType;
    private String cspAccount;
    private String instanceID;
    private String nsID;
    private String nsUID;
    private String vmID;
    private String vmUID;
    private String mciID;
    private String mciUID;
    private String instanceRunningStatus;
}
