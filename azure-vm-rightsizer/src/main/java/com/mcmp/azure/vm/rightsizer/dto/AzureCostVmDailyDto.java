package com.mcmp.azure.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AzureCostVmDailyDto {

    private Long id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String tenantId;
    private String subscriptionId;
    private Double preTaxCost;
    private String usageDate;
    private String resourceGroupName;
    private String resourceId;
    private String region;
    private String instanceType;
    private String osType;
    private String vmId;
    private String resourceGuid;
    private String currency;

    @Builder
    public AzureCostVmDailyDto(Long id, String tenantId, String subscriptionId, Double preTaxCost, String usageDate, String resourceGroupName, String resourceId, String region, String instanceType, String osType, String vmId, String resourceGuid, String currency) {
        this.id = id;
        this.tenantId = tenantId;
        this.subscriptionId = subscriptionId;
        this.preTaxCost = preTaxCost;
        this.usageDate = usageDate;
        this.resourceGroupName = resourceGroupName;
        this.resourceId = resourceId;
        this.region = region;
        this.instanceType = instanceType;
        this.osType = osType;
        this.vmId = vmId;
        this.resourceGuid = resourceGuid;
        this.currency = currency;
    }
}
