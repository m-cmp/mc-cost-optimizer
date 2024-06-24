package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BillingAssetReqModel {
    private String today;
    private List<String> selectedProjects;
    private List<String> selectedCsps;
    private String selectedWorkspace;

    private LocalDateTime curMonthStartDate;
    private LocalDateTime curMonthEndDate;

    private List<String> AWSChildProducts;
}
