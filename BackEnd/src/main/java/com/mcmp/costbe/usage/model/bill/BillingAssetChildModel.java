package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

@Data
public class BillingAssetChildModel {
    private String childProductCode;
    private Integer unit;
    private double bill;
}
