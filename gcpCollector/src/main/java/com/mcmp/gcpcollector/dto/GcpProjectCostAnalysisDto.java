package com.mcmp.gcpcollector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GcpProjectCostAnalysisDto {
    private String projectId;
    private String projectName;
    private String billingAccountId;
    private Double latestCost;      // 어제 비용
    private Double avgCost;         // 기준 일평균 (지난달 or 전체)
    private String dataRange;       // LAST_MONTH or ALL_DATA
    private String projectCd;       // servicegroup_meta.service_cd
    private String workspaceCd;     // servicegroup_meta.workspace_cd
    private String currency;
}
