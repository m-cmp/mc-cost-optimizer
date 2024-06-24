package com.mcmp.costbe.usage.model.bill;

import lombok.Data;

@Data
public class Top5BillModel {
    private double bill;
    private String resourceNm;
    private String csp;
    private Boolean isOthers;
}
