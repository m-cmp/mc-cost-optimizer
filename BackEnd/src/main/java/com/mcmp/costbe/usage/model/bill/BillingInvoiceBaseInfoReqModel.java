package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BillingInvoiceBaseInfoReqModel {
    private String today;
    private LocalDateTime curMonthStartDate;
    private LocalDateTime curMonthEndDate;
    private List<String> selectedCsps;
    private String selectedWorkspace;
    private List<String> selectedProjects;

}
