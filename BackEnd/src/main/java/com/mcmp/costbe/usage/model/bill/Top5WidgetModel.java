package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

import java.util.List;

@Data
public class Top5WidgetModel {
    private String curYear;
    private String curMonth;
    private List<Top5BillModel> top5bill;
    private List<String> selectedProjects;
    private List<String> selectedCsps;
}
