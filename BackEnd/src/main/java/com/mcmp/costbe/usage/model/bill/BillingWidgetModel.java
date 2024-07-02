package com.mcmp.costbe.usage.model.bill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "이번달 비용 조회 응답 모델")
public class BillingWidgetModel {
    @Schema(description = "년도", example = "2024")
    private String curYear;
    @Schema(description = "월", example = "06")
    private String curMonth;
    @Schema(description = "지난달 년도", example = "2024")
    private String prevYear;
    @Schema(description = "지난달", example = "05")
    private String prevMonth;
    @Schema(description = "이번달 비용 (USD)", example = "12.0")
    private Double curMonthBill;
    @Schema(description = "지난달 비용 (USD)", example = "2.0")
    private Double prevMonthBill;
    @Schema(description = "지난달 대비 비용 상승률 (%)", example = "6")
    private String momPer;
    @Schema(description = "지난달 대비 비용 상승값 (USD)", example = "10.0")
    private Double momBill;
    @Schema(description = "월별 비용")
    private List<MonthlyBillModel> monthlyBill;

    @Schema(description = "선택 프로젝트 코드", example = "[\"projectCode1\", \"projectCode2\"]")
    private List<String> selectedProjects;
    @Schema(description = "선택 CSP", example = "[\"AWS\"]")
    private List<String> selectedCsps;
}
