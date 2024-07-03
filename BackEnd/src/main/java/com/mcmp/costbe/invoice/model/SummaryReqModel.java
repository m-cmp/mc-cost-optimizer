package com.mcmp.costbe.invoice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "빌링 인보이스 날짜별 조회 요청 모델")
public class SummaryReqModel {
    @Schema(description = "오늘 날짜", example = "20240620", required = true)
    private String today;
    @Schema(description = "프로젝트 코드", example = "[\"projectCode1\", \"projectCode2\"]", required = true)
    private List<String> selectedProjects;
    @Schema(description = "CSP", example = "[\"AWS\"]", required = true)
    private List<String> selectedCsps;
    @Schema(description = "워크스페이스 코드", example = "workspaceCode1", required = true)
    private String selectedWorkspace;
    @Schema(description = "기간 선택 (7days, 3months, 30days)", example = "7days", required = true)
    private String selectedPeriod;

    @Schema(required = false)
    private List<LocalDateTime> summaryPeriod;
    @Schema(required = false)
    private LocalDateTime summaryPeriodDate;
}
