package com.mcmp.costbe.invoice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SummaryBillItemModel {
    @Schema(description = "CSP", example = "AWS")
    private String csp;
    @Schema(description = "비용")
    private List<Double> bill;

}
