package com.mcmp.costbe.opti.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InstOptiSizeRstModel {
    @Schema(description = "오늘 날짜", example = "20240608")
    private String today;

    @Schema(description = "인스턴스 추천 사이즈 목록")
    private List<InstOptiSizeItemModel> optiSizeItems;


    @Schema(description = "선택 프로젝트", example = "[\"projectCode1\", \"projectCode2\"]")
    private List<String> selectedProjects;

    @Schema(description = "선택 csp", example = "[\"AWS\"]")
    private List<String> selectedCsps;
}
