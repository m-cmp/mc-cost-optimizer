package com.mcmp.costbe.invoice.model;

import lombok.Data;

@Data
public class InvoiceItemModel {
    private String accountID;
    private String productID;
    private String csp;
    private double bill;
    private String resourceID;
}
