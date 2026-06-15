package com.mcmp.gcpcollector.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GcpBudgetUsageDto {

    /** 프로젝트 코드 (servicegroup_meta.service_cd or project_id fallback) */
    private String projectCd;

    /** CSP 타입 (GCP) */
    private String cspType;

    /** 이번 달 실제 사용 금액 */
    private BigDecimal totalCost;

    /** 설정된 예산 금액 */
    private BigDecimal budget;

    /** 통화 (USD) */
    private String currency;

    /** 예산 사용률 (%) */
    private BigDecimal usageRate;

    /** 청구 계정 ID */
    private String accountId;
}
