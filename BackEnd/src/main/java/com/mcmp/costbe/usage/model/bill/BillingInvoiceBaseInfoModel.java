package com.mcmp.costbe.usage.model.bill;

import lombok.Builder;
import lombok.Data;

@Data
public class BillingInvoiceBaseInfoModel {

    private String csp;
    private double cost;
    private String colorClass;
}
