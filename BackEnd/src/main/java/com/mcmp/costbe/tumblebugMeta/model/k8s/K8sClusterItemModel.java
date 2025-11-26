package com.mcmp.costbe.tumblebugMeta.model.k8s;

import lombok.Data;

@Data
public class K8sClusterItemModel {
    private String id;
    private String uid;
    private String name;
    private String description;
    private String resourceType;
    private String cspResourceId;
    private String cspResourceName;
    private String connectionName;
    private K8sConnectionConfigModel connectionConfig;
    private String status;
    private String version;
    private String createdTime;
}
