package com.mcmp.costbe.invoice.model;

import lombok.Data;

import java.util.List;

@Data
public class SummaryResModel {
    private String curMonth;
    private String curYear;
    private String curDay;
    private List<SummaryBillItemModel> summaryBill;
    private List<String> dates;
    private String selectedPeriod;
    private List<String> selectedProjects;
    private List<String> selectedCsps;
}
