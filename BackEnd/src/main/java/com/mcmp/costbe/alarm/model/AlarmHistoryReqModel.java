package com.mcmp.costbe.alarm.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AlarmHistoryReqModel {

    @Schema(description = "선택 csp", example = "[\"AWS\"]", required = true)
    private List<String> selectedCsps;
    @Schema(description = "선택 워크스페이스", example = "workspaceCode1", required = false, deprecated = true)
    private String selectedWorkspace;
    @Schema(description = "선택 프로젝트", example = "[\"projectCode1\", \"projectCode2\"]", required = true)
    private List<String> selectedProjects;

    @Schema(required = false)
    private LocalDate curDate;
}
