package com.mcmp.costbe.tumblebugMeta.model.k8s;

import lombok.Data;

@Data
public class K8sConnectionConfigModel {
    private String configName;
    private String credentialHolder;
    private String credentialName;
    private String driverName;
    private String providerName;
    private String regionZoneInfoName;
    private Boolean regionRepresentative;
    private Boolean verified;
}
