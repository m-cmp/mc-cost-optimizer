package com.mcmp.azure.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UnusedVmDto {

    /**
     * VM ID (csp_instanceid)
     */
    @JsonProperty(value = "vm_id")
    private String vmId;

    /**
     * CSP 계정 정보
     */
    @JsonProperty(value = "csp_account")
    private String cspAccount;

    /**
     * 어제 날짜의 평균 CPU 사용률
     */
    @JsonProperty(value = "yesterday_avg_cpu")
    private Double yesterdayAvgCpu;

    /**
     * 어제 날짜 (collect_dt)
     */
    @JsonProperty(value = "yesterday_date")
    private LocalDate yesterdayDate;

    /**
     * 14일간 평균 CPU 사용률
     */
    @JsonProperty(value = "avg_cpu_14days")
    private Double avgCpu14Days;

    /**
     * 14일간 최대 CPU 사용률
     */
    @JsonProperty(value = "max_cpu_14days")
    private Double maxCpu14Days;

    /**
     * 14일간 데이터 수집 일수
     */
    @JsonProperty(value = "day_count")
    private Integer dayCount;

    /**
     * Unused 등급 (Unused 조건 충족 시에만 값 존재)
     */
    @JsonProperty(value = "unused_rating")
    private String unusedRating;

    /**
     * 프로젝트 코드
     */
    @JsonProperty(value = "project_cd")
    private String projectCd;

    /**
     * 워크스페이스 코드
     */
    @JsonProperty(value = "workspace_cd")
    private String workspaceCd;

    /**
     * CSP 타입 (AZURE, NCP)
     */
    @JsonProperty(value = "csp_type")
    private String cspType;

    @Builder
    public UnusedVmDto(String vmId, String cspAccount, Double yesterdayAvgCpu, LocalDate yesterdayDate,
                       Double avgCpu14Days, Double maxCpu14Days, Integer dayCount, String unusedRating,
                       String projectCd, String workspaceCd, String cspType) {
        this.vmId = vmId;
        this.cspAccount = cspAccount;
        this.yesterdayAvgCpu = yesterdayAvgCpu;
        this.yesterdayDate = yesterdayDate;
        this.avgCpu14Days = avgCpu14Days;
        this.maxCpu14Days = maxCpu14Days;
        this.dayCount = dayCount;
        this.unusedRating = unusedRating;
        this.projectCd = projectCd;
        this.workspaceCd = workspaceCd;
        this.cspType = cspType;
    }
}
