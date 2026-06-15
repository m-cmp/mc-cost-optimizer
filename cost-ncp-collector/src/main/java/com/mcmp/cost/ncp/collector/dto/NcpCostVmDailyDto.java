package com.mcmp.cost.ncp.collector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NcpCostVmDailyDto {
    private Long id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String memberNo;
    private String demandMonth;
    private String regionCode;
    private String serverSpecCode;
    private String instanceNo;
    private String instanceName;
    private String usageUnitCode;
    private String usageUnitName;
    private Double productPrice;
    private Double unitUsageQuantity;
    private Double totalUnitUsageQuantity;
    private Double useAmount;
    private Double demandAmount;
    private LocalDateTime writeDate;
    private String payCurrency;
    private LocalDate targetDate;
    private Double dailyChargeAmount;

    @Builder
    public NcpCostVmDailyDto(Long id, LocalDateTime created, LocalDateTime updated, String memberNo, String demandMonth, String regionCode, String serverSpecCode, String instanceNo, String instanceName, String usageUnitCode, String usageUnitName, Double productPrice, Double unitUsageQuantity, Double totalUnitUsageQuantity, Double useAmount, Double demandAmount, LocalDateTime writeDate, String payCurrency, LocalDate targetDate, Double dailyChargeAmount) {
        this.id = id;
        this.created = created;
        this.updated = updated;
        this.memberNo = memberNo;
        this.demandMonth = demandMonth;
        this.regionCode = regionCode;
        this.serverSpecCode = serverSpecCode;
        this.instanceNo = instanceNo;
        this.instanceName = instanceName;
        this.usageUnitCode = usageUnitCode;
        this.usageUnitName = usageUnitName;
        this.productPrice = productPrice;
        this.unitUsageQuantity = unitUsageQuantity;
        this.totalUnitUsageQuantity = totalUnitUsageQuantity;
        this.useAmount = useAmount;
        this.demandAmount = demandAmount;
        this.writeDate = writeDate;
        this.payCurrency = payCurrency;
        this.targetDate = targetDate;
        this.dailyChargeAmount = dailyChargeAmount;
    }
}
