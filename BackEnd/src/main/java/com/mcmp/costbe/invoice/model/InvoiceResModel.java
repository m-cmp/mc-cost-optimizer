package com.mcmp.costbe.invoice.model;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceResModel {
    private String curMonth;
    private String curYear;
    private List<InvoiceItemModel> invoice;
    private List<String> selectedProjects;
    private List<String> selectedCsps;
}
