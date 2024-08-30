package com.mcmp.costbe.usage.model.bill;

import com.mcmp.costbe.common.model.PrevMonthsModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "이번달 비용 조회 요청 모델")
public class BillingWidgetReqModel extends PrevMonthsModel {

    @Schema(description = "오늘 날짜", example = "20240620", required = true)
    private String today;

    @Schema(description = "프로젝트 코드", example = "[\"projectCode1\", \"projectCode2\"]", required = true)
    private List<String> selectedProjects;

    @Schema(description = "CSP", example = "[\"AWS\"]", required = true)
    private List<String> selectedCsps;

    @Schema(description = "워크스페이스 코드", example = "workspaceCode1", required = true)
    private String selectedWorkspace;

    @Schema(required = false)
    private LocalDateTime curMonthStartDate;
    @Schema(required = false)
    private LocalDateTime curMonthEndDate;
    @Schema(required = false)
    private LocalDateTime prevMonthStartDate;
    @Schema(required = false)
    private LocalDateTime prevMonthEndDate;

    @Schema(required = false)
    private String curYearMonth;

    @Schema(required = false)
    private String prevYearMonth;
}
