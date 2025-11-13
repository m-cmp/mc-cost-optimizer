package com.mcmp.cost.azure.collector.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class BudgetUsageDto {
    /**
     * 프로젝트 코드
     */
    private String projectCd;

    /**
     * CSP 타입 (AZURE, NCP, AWS)
     */
    private String cspType;

    /**
     * 실제 사용 금액
     */
    private BigDecimal totalCost;

    /**
     * 설정된 예산 금액
     */
    private BigDecimal budget;

    /**
     * 통화 (USD, KRW)
     */
    private String currency;

    /**
     * 예산 사용률 (%)
     */
    private BigDecimal usageRate;

    /**
     * VM 개수 (선택사항)
     */
    private Integer vmCount;

    /**
     * 계정 ID (Azure: subscription_id, NCP: member_no)
     */
    private String accountId;
}
