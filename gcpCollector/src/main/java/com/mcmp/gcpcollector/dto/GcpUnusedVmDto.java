package com.mcmp.gcpcollector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GcpUnusedVmDto {

    // asset_compute_metric 조회 결과
    private String    vmId;             // csp_instanceid
    private String    billingAccountId; // csp_account
    private Double    yesterdayAvgCpu;
    private LocalDate yesterdayDate;
    private String    projectCd;        // servicegroup_meta.service_cd (fallback: csp_instanceid)
    private String    workspaceCd;

    // unused_daily_mart 14일 통계
    private Double  avgCpu14Days;
    private Double  maxCpu14Days;
    private Integer dayCount;

    private String unusedRating;        // "Unused" or null
}
