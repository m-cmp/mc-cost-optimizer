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
    @Schema(description = "메타 분류(servicegroup_meta.service_type). K8S 이면서 K8s 서비스가 아닌 줄(AmazonEC2, Compute Engine 등)은 클러스터 노드 비용", example = "K8S")
    private String serviceType;
}
