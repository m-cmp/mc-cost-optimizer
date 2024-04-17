package com.mcmp.dummybe.model.billing;

import lombok.Data;

@Data
public class DetailsDataModel {
    private String linkedAccountId;
    private String linkedAccountAlias;
    private String productName;
    private String regionName;
    private String usageType;
    private String invoiceId;
    private String tagValue;
    private String tagKey;
    private double usage;
    private double cost;
    private String itemDescription;
    private String serviceGroup;
    private String domainName;
    private String invoiceType;
}
