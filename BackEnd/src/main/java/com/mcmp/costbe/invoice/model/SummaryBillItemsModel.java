package com.mcmp.costbe.invoice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class SummaryBillItemsModel {
    @Schema(description = "CSP", example = "AWS")
    private String csp;
    @Schema(description = "해당 CSP에 대한 비용 월별 목록")
    private List<SummaryBillItemModel> monthlyBill;

    public SummaryBillItemsModel(String csp, List<SummaryBillItemModel> monthlyBill){
        this.csp = csp;
        this.monthlyBill = monthlyBill;
    }
}
