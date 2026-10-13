package com.mcmp.costbe.tumblebugMeta.model.k8s;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class K8sConnectionConfigModel {
    private String configName;
    private String credentialHolder;
    private String credentialName;
    private String driverName;
    private String providerName;
    private String regionZoneInfoName;
    private RegionZoneInfo regionZoneInfo;
    private Boolean regionRepresentative;
    private Boolean verified;

    /** Tumblebug connectionConfig.regionZoneInfo — CSP 리전/존 이름 (예: assignedRegion = ap-northeast-2) */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RegionZoneInfo {
        private String assignedRegion;
        private String assignedZone;
    }
}
