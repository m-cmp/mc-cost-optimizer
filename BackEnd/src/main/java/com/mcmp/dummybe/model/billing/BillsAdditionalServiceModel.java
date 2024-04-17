package com.mcmp.dummybe.model.billing;

import lombok.Data;

@Data
public class BillsAdditionalServiceModel {
    private String additionalServiceName;
    private String additionalServiceCode;
    private double additionalServiceCharge;

}