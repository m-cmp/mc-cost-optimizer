package com.mcmp.costbe.tumblebugMeta.model.mci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbVmConnectionConfigModel {
    private String configName;
    private String credentialHolder;
    private String credentialName;
    private String driverName;
    private String providerName;
    private Object regionDetail;
    private Boolean regionRepresentative;
    private Object regionZoneInfo;
    private String regionZoneInfoName;
    private Boolean verified;
}
