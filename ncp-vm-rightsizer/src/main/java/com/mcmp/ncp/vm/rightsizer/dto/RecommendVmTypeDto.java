package com.mcmp.ncp.vm.rightsizer.dto;

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
public class RecommendVmTypeDto {

    private String currentType;
    private String recommendType;
    private String memberNo;
    private String vmId;
    private Double core;
    private Double memory;
    private Double usd;
    private String plan;  // "UP" or "DOWN"
    private String projectCd;  // servicegroup_meta.service_cd (후보 쿼리가 csp_instanceid 조인으로 이미 구함)

    @Builder
    public RecommendVmTypeDto(String currentType, String recommendType, String memberNo, String vmId, Double core, Double memory, Double usd, String plan) {
        this.currentType = currentType;
        this.recommendType = recommendType;
        this.memberNo = memberNo;
        this.vmId = vmId;
        this.core = core;
        this.memory = memory;
        this.usd = usd;
        this.plan = plan;
    }
}
