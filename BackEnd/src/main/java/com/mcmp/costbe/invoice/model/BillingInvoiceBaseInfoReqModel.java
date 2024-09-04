package com.mcmp.costbe.invoice.model;

import com.mcmp.costbe.usage.model.bill.YearMonthModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "빌링 인보이스 요약 조회 요청 모델")
public class BillingInvoiceBaseInfoReqModel extends YearMonthModel {
    @Schema(description = "오늘 날짜", example = "20240620", required = true)
    private String today;
    @Schema(required = false)
    private LocalDateTime curMonthStartDate;
    @Schema(required = false)
    private LocalDateTime curMonthEndDate;
    @Schema(description = "CSP", example = "[\"AWS\"]", required = true)
    private List<String> selectedCsps;
    @Schema(description = "워크스페이스 코드", example = "workspaceCode1", required = true)
    private String selectedWorkspace;
    @Schema(description = "프로젝트 코드", example = "[\"projectCode1\", \"projectCode2\"]", required = true)
    private List<String> selectedProjects;

}
