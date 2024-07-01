package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

import java.util.List;

@Data
public class BillingAssetModel {

    private String familyProductCode;
    private List<BillingAssetChildModel> childProductCode;
    private Integer totalUnit;
    private double totalCost;

}
