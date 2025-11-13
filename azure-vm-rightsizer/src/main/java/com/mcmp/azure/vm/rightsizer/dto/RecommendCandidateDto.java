package com.mcmp.azure.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendCandidateDto {

    private String resourceId;           // vm_id
    private String subscriptionId;       // Azure subscription_id
    private Double avg4DaysCpu;          // 4일 평균 CPU
    private Double max4DaysCpu;          // 4일 최대 CPU
    private String region;               // region
    private String instanceType;         // instance_type (현재 스펙)
    private String vmId;                 // vm_id
    private String osType;               // OS 타입 (WINDOWS/LINUX) - Azure only
    private String recommendType;        // "Up" or "Down"

    @Builder
    public RecommendCandidateDto(String resourceId, String subscriptionId, Double avg4DaysCpu,
                                 Double max4DaysCpu, String region, String instanceType,
                                 String vmId, String osType, String recommendType) {
        this.resourceId = resourceId;
        this.subscriptionId = subscriptionId;
        this.avg4DaysCpu = avg4DaysCpu;
        this.max4DaysCpu = max4DaysCpu;
        this.region = region;
        this.instanceType = instanceType;
        this.vmId = vmId;
        this.osType = osType;
        this.recommendType = recommendType;
    }
}
