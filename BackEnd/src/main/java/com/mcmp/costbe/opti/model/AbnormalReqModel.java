package com.mcmp.costbe.opti.model;

import com.mcmp.costbe.common.model.LocalDateModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "이상 비용 조회 요청 모델")
public class AbnormalReqModel extends LocalDateModel {
    @Schema(description = "오늘 날짜", example = "20240608", required = true)
    private String today;

    @Schema(description = "선택 워크스페이스", example = "workspaceCode1", required = true)
    private String selectedWorkspace;

    @Schema(description = "선택 프로젝트", example = "[\"projectCode1\", \"projectCode2\"]", required = true)
    private List<String> selectedProjects;

    @Schema(description = "선택 csp", example = "[\"AWS\"]", required = false)
    private List<String> selectedCsps;
}
