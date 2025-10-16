package com.mcmp.costbe.budget.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CSP별 금액 정보 모델")
public class CspAmountModel {

    @Schema(description = "전체 합계", example = "1000.00")
    private Double total;

    @JsonProperty("AWS")
    @Schema(description = "AWS 금액", example = "500.00")
    private Double AWS;

    @JsonProperty("NCP")
    @Schema(description = "NCP 금액 (USD 환산)", example = "300.00")
    private Double NCP;

    @JsonProperty("Azure")
    @Schema(description = "Azure 금액 (USD 환산)", example = "200.00")
    private Double Azure;
}
