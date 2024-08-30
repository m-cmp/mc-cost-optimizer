package com.mcmp.costbe.invoice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SummaryBillItemModel {

    private String csp;
    @Schema(description = "연도월", example = "202406")
    private String yearMonth;
    @Schema(description = "비용")
    private Double bill;

}
