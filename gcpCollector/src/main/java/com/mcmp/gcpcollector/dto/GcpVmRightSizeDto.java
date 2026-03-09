package com.mcmp.gcpcollector.dto;

import lombok.Data;

@Data
public class GcpVmRightSizeDto {

    // unused_daily_mart + servicegroup_meta 조회 결과
    private String  vmId;             // csp_instanceid
    private String  billingAccountId; // csp_account
    private Double  avg4DaysCpu;
    private Double  max4DaysCpu;
    private Integer dayCount;
    private String  recommendType;    // "Up" or "Down"
    private String  projectCd;
    private String  workspaceCd;

    // servicegroup_meta의 Tumblebug 식별자
    private String tbbNsId;           // service_cd (Tumblebug namespace)
    private String tbbMciId;          // mci_id
    private String tbbVmId;           // vm_id

    // Tumblebug 스펙 조회 결과
    private String  currentSpecId;
    private String  currentSpecName;
    private Integer currentVcpu;
    private Double  currentMemoryGiB;
    private String  regionName;        // VM 리전 (추천 필터용)

    // Tumblebug 추천 결과
    private String recommendSpecId;
    private String recommendSpecName;
}
