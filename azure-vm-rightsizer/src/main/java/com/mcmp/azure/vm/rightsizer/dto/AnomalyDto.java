package com.mcmp.azure.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AnomalyDto {

    /**
     * 수집한 날짜.
     */
    @JsonProperty(value = "collect_dt")
    private LocalDateTime collectDt;

    /**
     * vm id
     */
    @JsonProperty(value = "vm_id")
    private String vmId;

    /**
     * 서비스 코드 ex) AmazonEC2, Virtual Machine
     */
    @JsonProperty(value = "product_cd")
    private String productCd;

    /**
     * 이상비용 위험도 ex) Alarm
     */
    @JsonProperty(value = "abnormal_rating")
    private String abnormalRating;

    /**
     * 지난달 사용 차이률 ex) 200%
     */
    @JsonProperty(value = "percentage_point")
    private double percentagePoint;

    /**
     * 오늘 과금 비용(USD) ex) 20.0
     */
    @JsonProperty(value = "standard_cost")
    private double standardCost;

    /**
     * 지난달 사용 과금 비용 평균(USD) ex) 10.0
     */
    @JsonProperty(value = "subject_cost")
    private double subjectCost;

    /**
     * 프로젝트 아이디 TODO: 확인 필요
     */
    @JsonProperty(value = "project_cd")
    private String projectCd;

    /**
     * 워크스페이스 아이디 TODO: 확인 필요
     */
    @JsonProperty(value = "workspace_cd")
    private String workspaceCd;

    /**
     * csp 종류 ex) AWS, AZURE, NCP
     */
    @JsonProperty(value = "csp_type")
    private String cspType;

    @Builder

    public AnomalyDto(LocalDateTime collectDt, String vmId, String productCd, String abnormalRating, double percentagePoint, double standardCost, double subjectCost, String projectCd, String workspaceCd, String cspType) {
        this.collectDt = collectDt;
        this.vmId = vmId;
        this.productCd = productCd;
        this.abnormalRating = abnormalRating;
        this.percentagePoint = percentagePoint;
        this.standardCost = standardCost;
        this.subjectCost = subjectCost;
        this.projectCd = projectCd;
        this.workspaceCd = workspaceCd;
        this.cspType = cspType;
    }
}
