package com.mcmp.dummybe.model.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
@Data
public class ChargesDataModel {
    private String linkedAccountId;
    private String linkedAccountAlias;
    private String vendor;
    private String chargeYear;
    private String chargeMonth;
    private double totalCharge;
    private String invoiceCurrency;
    private String companyCurrency;
    private double cloudCost;
    private double cloudOriginalCost;
    private double onDemandDiscount;
    private double cloudFrontDiscount;
    private double cloudFrontDtoDiscount;
    private double cloudFrontReqDiscount;
    private double cloudServiceCharge;
    private double exchangedCloudServiceCharge;
    private double salesDiscount;
    private String salesDiscountApplyType;
    private double salesDiscountApplyValue;
    private double supportFee;
    private double supportFeeApplyValue;
    private double credit;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ChargesAdditionalServiceModel> additionalServices;
}