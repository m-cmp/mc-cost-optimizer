package com.mcmp.dummybe.model.billing;

import lombok.Data;

@Data
public class ChargesAdditionalServiceModel {
    private String additionalServiceName;
    private String additionalServiceCode;
    private double additionalServiceCharge;

}
