package com.mcmp.costbe.usage.model.bill;


import lombok.Data;

import java.util.List;

@Data
public class BillingAssetWidgetModel {

    private String curYear;
    private String curMonth;
    private List<BillingAssetModel> billingAsset;
    private List<String> selectedProjects;
    private List<String> selectedCsps;
}
