package com.mcmp.costselector.unused.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UnusedResourceStatusModel {
    private String resource_id;
    private String csp_type;
    private String csp_account;
    private String cmp_user_id;
    private String resource_status;
    private String resource_spot_yn;
    private LocalDateTime collect_dt;
    private String instance_type;
    private String rsrc_type;
    private String region_id;
    private String region_nm;
    private String operation;
    private String inst_family;
    private String inst_family_type;
    private String product_sku;
    private String service_cd;   // servicegroup_meta.service_cd = 알림 project_cd (없으면 default fallback)

    private Boolean isDownsizeTarget;
    private Boolean isUpsizeTarget;

    // Tumblebug 식별자 (servicegroup_meta JOIN으로 채워짐)
    private String tbbNsId;
    private String tbbMciId;
    private String tbbVmId;

    // Tumblebug API로 채워지는 현재 스펙 정보
    private String currentSpecName;
    private Integer currentVcpu;
    private Double currentMemGiB;
    private Double currentCostPerHour;
    private Double recommendCostPerHour;
    private String tbbRegionName;
}
