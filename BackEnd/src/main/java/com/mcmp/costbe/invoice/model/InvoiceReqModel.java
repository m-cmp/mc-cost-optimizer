package com.mcmp.costbe.invoice.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceReqModel {
    private String today;
    private List<String> selectedProjects;
    private List<String> selectedCsps;
    private String selectedWorkspace;

    private LocalDateTime curMonthStartDate;
    private LocalDateTime curMonthEndDate;
}
