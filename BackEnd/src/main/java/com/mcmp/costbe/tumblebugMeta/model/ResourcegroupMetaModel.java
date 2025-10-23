package com.mcmp.costbe.tumblebugMeta.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourcegroupMetaModel {
    private String cspType;
    private String cspAccount;
    private String cspInstanceid;
    private String serviceCd;
    private String serviceNm;
    private String serviceUid;
    private String workspaceCd;
    private String vmId;
    private String vmUid;
    private String vmNm;
    private String mciId;
    private String mciUid;
    private String mciNm;
    private String instanceRunningStatus;
}
