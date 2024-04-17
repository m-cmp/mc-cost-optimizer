package com.mcmp.dummybe.model.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BillsDataModel {
    private String chargeYear;
    private String chargeMonth;
    private Date startDate;
    private Date endDate;
    private double increaseDecreaseRate;
    private double totalCharge;
    private String invoiceCurrency;
    private String companyCurrency;
    private double applyExchangeRate;
    private Date applyExchangeRateDate;
    private double cloudCost;
    private double cloudOriginalCost;
    private double cloudUseOriginalCost;
    private double onDemandDiscount;
    private double cloudFrontDiscount;
    private double supportFee;
    private double salesDiscount;
    private double credit;
    private double cloudServiceCharge;
    private double exchangedCloudServiceCharge;
    private double additionalServiceCharge;
    private char vatYn;
    private char billConfirmationYn;
    private Date lastBillUpdateDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<BillsAdditionalServiceModel> additionalServices;
}
