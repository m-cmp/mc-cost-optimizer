package com.mcmp.azure.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * VM 비용 이상 탐지를 위한 기준 데이터 (Baseline)
 * - 지난달 데이터가 있으면 지난달 평균
 * - 지난달 데이터가 없으면 수집된 전체 데이터 평균
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VmMonthlyAvgCostDto {

    /**
     * subscription ID
     */
    private String subscriptionId;

    /**
     * VM ID
     */
    private String vmId;

    /**
     * 평균 비용
     */
    private Double avgCost;

    /**
     * 일수
     */
    private Integer dayCount;

    /**
     * 총 비용
     */
    private Double totalCost;

    /**
     * 마지막날 비용
     */
    private Double latestCost;

    /**
     * 시작 날짜 (ex: 20250801)
     */
    private String fromDate;

    /**
     * 종료 날짜 (ex: 20250831)
     */
    private String toDate;

    /**
     * 데이터 범위 (LAST_MONTH / ALL_DATA)
     */
    private String dataRange;

    @Builder
    public VmMonthlyAvgCostDto(String subscriptionId, String vmId, Double avgCost, Integer dayCount, Double totalCost, Double latestCost, String fromDate, String toDate, String dataRange) {
        this.subscriptionId = subscriptionId;
        this.vmId = vmId;
        this.avgCost = avgCost;
        this.dayCount = dayCount;
        this.totalCost = totalCost;
        this.latestCost = latestCost;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.dataRange = dataRange;
    }
}
