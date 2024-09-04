package com.mcmp.costbe.invoice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "빌링 인보이스 날짜별 조회 응답 모델")
public class SummaryResModel {
    @Schema(description = "월", example = "06")
    private String curMonth;
    @Schema(description = "년도", example = "2024")
    private String curYear;
    @Schema(description = "csp 별 비용")
    private List<SummaryBillItemsModel> summaryBill;
    @Schema(description = "지난 12달 목록", example = "[\"202406\",\"202405\",\"202404\", ... ,\"202307\"]")
    private List<String> yearMonths;
    @Schema(description = "선택 프로젝트 코드", example = "[\"projectCode1\", \"projectCode2\"]")
    private List<String> selectedProjects;
    @Schema(description = "선택 CSP", example = "[\"AWS\"]")
    private List<String> selectedCsps;
}
