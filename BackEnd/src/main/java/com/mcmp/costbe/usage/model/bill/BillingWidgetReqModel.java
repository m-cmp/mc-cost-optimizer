package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BillingWidgetReqModel {
    private String today;
    private List<String> selectedProjects;
    private List<String> selectedCsps;
    private String selectedWorkspace;

    private LocalDateTime curMonthStartDate;
    private LocalDateTime curMonthEndDate;
    private LocalDateTime prevMonthStartDate;
    private LocalDateTime prevMonthEndDate;
}
