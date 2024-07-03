package com.mcmp.costbe.invoice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class InvoiceItemModel {
    @Schema(description = "Account ID", example = "123456789")
    private String accountID;
    @Schema(description = "리소스", example = "AmazonS3")
    private String productID;
    @Schema(description = "CSP", example = "AWS")
    private String csp;
    @Schema(description = "비용", example = "0.25435")
    private double bill;
    @Schema(description = "리소스 고유 ID", example = "TEST-RESOURCE-ID")
    private String resourceID;
}
