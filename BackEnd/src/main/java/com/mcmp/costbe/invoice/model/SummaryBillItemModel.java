package com.mcmp.costbe.invoice.model;

import lombok.Data;

import java.util.List;

@Data
public class SummaryBillItemModel {
    private String csp;
    private List<Double> bill;

}
