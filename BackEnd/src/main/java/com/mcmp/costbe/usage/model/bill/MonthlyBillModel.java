package com.mcmp.costbe.usage.model.bill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class MonthlyBillModel {
    @Schema(description = "월", example = "06")
    private String month;
    @Schema(description = "년도", example = "2024")
    private String year;
    @Schema(description = "비용", example = "12.0")
    private double bill;
}
