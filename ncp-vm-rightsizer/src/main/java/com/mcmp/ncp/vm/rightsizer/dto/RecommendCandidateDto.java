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
    private String regionCode;           // 리전 코드
    private String serverSpecCode;       // 서버 스펙 코드 (현재 타입)
    private String instanceName;
    private String recommendType;        // "UP" or "DOWN"

    @Builder
    public RecommendCandidateDto(String resourceId, String memberNo, Double avg4DaysCpu,
                                 Double max4DaysCpu, String regionCode, String serverSpecCode,
                                 String instanceName, String recommendType) {
        this.resourceId = resourceId;
        this.memberNo = memberNo;
        this.avg4DaysCpu = avg4DaysCpu;
        this.max4DaysCpu = max4DaysCpu;
        this.regionCode = regionCode;
        this.serverSpecCode = serverSpecCode;
        this.instanceName = instanceName;
        this.recommendType = recommendType;
    }
}
