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
    @Schema(description = "일", example = "20")
    private String curDay;
    @Schema(description = "csp 별 비용")
    private List<SummaryBillItemModel> summaryBill;
    @Schema(description = "선택 날짜 목록", example = "[\"2024-06-13\",\"2024-06-14\",\"2024-06-15\",\"2024-06-16\",\"2024-06-17\",\"2024-06-18\",\"2024-06-19\"]")
    private List<String> dates;
    @Schema(description = "선택 기간", example = "7days")
    private String selectedPeriod;
    @Schema(description = "선택 프로젝트 코드", example = "[\"projectCode1\", \"projectCode2\"]")
    private List<String> selectedProjects;
    @Schema(description = "선택 CSP", example = "[\"AWS\"]")
    private List<String> selectedCsps;
}
