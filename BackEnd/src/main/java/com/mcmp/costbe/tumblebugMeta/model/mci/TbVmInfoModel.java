package com.mcmp.costbe.tumblebugMeta.model.mci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbVmInfoModel {
    private List<Map<String, Object>> addtionalDetails;
    private TbVmConnectionConfigModel connectionConfig;
    private String connectionName;
    private String createdTime;
    private String cspImageName;
    private String cspResourceId;
    private String cspResourceName;
    private String cspSpecName;
    private String cspSshKeyId;
    private String cspSubnetId;
    private String cspVNetId;
    private List<String> dataDiskIds;
    private String description;
    private String id;
    private String imageId;
    private Map<String, String> label;
    private Object location;
    private String monAgentStatus;
    private String name;
    private String networkAgentStatus;
    private String networkInterface;
    private String privateDNS;
    private String privateIP;
    private String publicDNS;
    private String publicIP;
    private Object region;
    private String resourceType;
    private String specId;
    private String sshKeyId;
    private String status;
    private String subGroupId;
    private String subnetId;
    private String targetAction;
    private String targetStatus;
    private String uid;
    private String vNetId;
    private String vmUserName;
    private String vmUserPassword;

}
