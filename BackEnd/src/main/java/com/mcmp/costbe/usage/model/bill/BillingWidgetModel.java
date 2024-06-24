package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

import java.util.List;

@Data
public class BillingWidgetModel {
    private String curYear;
    private String curMonth;
    private String prevYear;
    private String prevMonth;
    private Double curMonthBill;
    private Double prevMonthBill;
    private String momPer;
    private Double momBill;
    private List<MonthlyBillModel> monthlyBill;

    private List<String> selectedProjects;
    private List<String> selectedCsps;
}
