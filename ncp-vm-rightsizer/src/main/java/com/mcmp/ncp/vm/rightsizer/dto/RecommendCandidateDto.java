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
public class RecommendCandidateDto {

    private String resourceId;           // instance_no
    private String memberNo;
    private Double avg4DaysCpu;          // 4일 평균 CPU
    private Double max4DaysCpu;          // 4일 최대 CPU
    private String regionCode;           // 리전 코드 (Modernize용 유지)
    private String serverSpecCode;       // 서버 스펙 코드 (Modernize용 유지)
    private String instanceName;
    private String recommendType;        // "Up" | "Down" | "Modernize"

    // Tumblebug 식별자 (servicegroup_meta에서 조회)
    private String tbbNsId;             // service_cd
    private String tbbMciId;            // mci_id
    private String tbbVmId;             // vm_id

    // Tumblebug 스펙 조회 결과 (TumblebugClient가 채움)
    private String  currentSpecName;
    private Integer currentVcpu;
    private Double  currentMemGiB;
    private Double  currentCostPerHour;
    private Double  recommendCostPerHour;
    private String  regionName;

    @Builder
    public RecommendCandidateDto(String resourceId, String memberNo, Double avg4DaysCpu,
                                 Double max4DaysCpu, String regionCode, String serverSpecCode,
                                 String instanceName, String recommendType,
                                 String tbbNsId, String tbbMciId, String tbbVmId) {
        this.resourceId = resourceId;
        this.memberNo = memberNo;
        this.avg4DaysCpu = avg4DaysCpu;
        this.max4DaysCpu = max4DaysCpu;
        this.regionCode = regionCode;
        this.serverSpecCode = serverSpecCode;
        this.instanceName = instanceName;
        this.recommendType = recommendType;
        this.tbbNsId = tbbNsId;
        this.tbbMciId = tbbMciId;
        this.tbbVmId = tbbVmId;
    }
}
