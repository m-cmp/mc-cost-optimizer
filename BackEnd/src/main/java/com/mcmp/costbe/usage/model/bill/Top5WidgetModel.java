package com.mcmp.costbe.usage.model.bill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "이번달 상위 5개 리소스 비용 조회 응답 모델")
public class Top5WidgetModel {
    @Schema(description = "년도", example = "2024")
    private String curYear;
    @Schema(description = "월", example = "06")
    private String curMonth;
    @Schema(description = "상위 5개 리소스 비용")
    private List<Top5BillModel> top5bill;
    @Schema(description = "선택 프로젝트 코드", example = "[\"projectCode1\", \"projectCode2\"]")
    private List<String> selectedProjects;
    @Schema(description = "선택 CSP", example = "[\"AWS\"]")
    private List<String> selectedCsps;
}
