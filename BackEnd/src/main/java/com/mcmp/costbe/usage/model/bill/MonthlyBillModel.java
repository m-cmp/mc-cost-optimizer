package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

import java.util.List;

@Data
public class MonthlyBillModel {
    private String month;
    private String year;
    private double bill;
}
