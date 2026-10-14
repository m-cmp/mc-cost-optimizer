package com.mcmp.gcpcollector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GcpVmCostAnalysisDto {
    private String vmId;            // gcp_billing_raw.csp_instanceid (매핑된 리소스만)
    private String billingAccountId;
    private Double latestCost;      // 어제 비용
    private Double avgCost;         // 지난달 같은 요일 평균
    private String projectCd;       // servicegroup_meta.service_cd
    private String workspaceCd;     // servicegroup_meta.workspace_cd
}
