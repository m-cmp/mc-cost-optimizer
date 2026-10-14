package com.mcmp.gcpcollector.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GcpAnomalyDto {
    private LocalDateTime collectDt;
    private String vmId;                // csp_instanceid
    private String productCd;           // = vmId (daily_abnormal_by_product PK 컬럼)
    private String abnormalRating;      // Critical / Caution / Warning / null
    private double percentagePoint;     // 변화율 (%)
    private double standardCost;        // 최신 비용 (어제)
    private double subjectCost;         // 기준 평균 비용
    private String projectCd;
    private String workspaceCd;
    private String cspType;             // "GCP"
}
