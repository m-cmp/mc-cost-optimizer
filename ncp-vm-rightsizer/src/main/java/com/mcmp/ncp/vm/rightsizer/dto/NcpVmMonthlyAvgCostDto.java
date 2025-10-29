package com.mcmp.ncp.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NcpVmMonthlyAvgCostDto {

    private String memberNo;
    private String instanceNo;
    private String instanceName;
    private Double avgCost;        // 평균 일일 비용
    private Integer dayCount;      // 집계 일수
    private Double latestCost;     // 가장 최근 일일 비용
    private Double totalCost;      // 총 비용
    private LocalDate fromDate;    // 집계 시작일
    private LocalDate toDate;      // 집계 종료일
    private String dataRange;      // LAST_MONTH 또는 ALL_DATA

    @Builder
    public NcpVmMonthlyAvgCostDto(String memberNo, String instanceNo, String instanceName, Double avgCost, Integer dayCount, Double latestCost, Double totalCost, LocalDate fromDate, LocalDate toDate, String dataRange) {
        this.memberNo = memberNo;
        this.instanceNo = instanceNo;
        this.instanceName = instanceName;
        this.avgCost = avgCost;
        this.dayCount = dayCount;
        this.latestCost = latestCost;
        this.totalCost = totalCost;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.dataRange = dataRange;
    }
}
