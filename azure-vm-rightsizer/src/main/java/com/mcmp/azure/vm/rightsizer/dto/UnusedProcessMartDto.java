package com.mcmp.azure.vm.rightsizer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UnusedProcessMartDto {

    /**
     * 생성일시 (배치 실행 시점)
     */
    @JsonProperty(value = "create_dt")
    private LocalDateTime createDt;

    /**
     * 리소스 ID (csp_instanceid)
     */
    @JsonProperty(value = "resource_id")
    private String resourceId;

    /**
     * 수집 날짜 (어제 날짜)
     */
    @JsonProperty(value = "collect_dt")
    private LocalDate collectDt;

    /**
     * 메트릭 타입 (cpu, network 등)
     */
    @JsonProperty(value = "metric_type")
    private String metricType;

    /**
     * 메트릭 평균값
     */
    @JsonProperty(value = "metric_avg_amount")
    private Double metricAvgAmount;

    @Builder
    public UnusedProcessMartDto(LocalDateTime createDt, String resourceId, LocalDate collectDt,
                                String metricType, Double metricAvgAmount) {
        this.createDt = createDt;
        this.resourceId = resourceId;
        this.collectDt = collectDt;
        this.metricType = metricType;
        this.metricAvgAmount = metricAvgAmount;
    }
}
