package com.mcmp.ncp.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NcpCostVmMonthDto  {

    private Long id;
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
    private Date writeDate;
    private String payCurrency;

    @Builder
    public NcpCostVmMonthDto(Long id, String memberNo, String demandMonth, String serverSpecCode, String regionCode, String instanceNo, String instanceName, String usageUnitCode, String usageUnitName, Double productPrice, Double unitUsageQuantity, Double totalUnitUsageQuantity, Double useAmount, Double demandAmount, Date writeDate, String payCurrency) {
        this.id = id;
        this.memberNo = memberNo;
        this.demandMonth = demandMonth;
        this.serverSpecCode = serverSpecCode;
        this.regionCode = regionCode;
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
    }
}
