package com.mcmp.costbe.usage.model.bill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "빌링 인보이스 요약 조회 응답 모델")
public class BillingInvoiceBaseInfoModel {
    @Schema(description = "CSP", example = "AWS")
    private String csp;
    @Schema(description = "비용", example = "73.4206")
    private double cost;
    @Schema(description = "color", example = "red")
    private String colorClass;
}
