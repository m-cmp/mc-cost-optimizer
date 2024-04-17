package com.mcmp.dummybe.model.common;

import lombok.Data;

import java.io.IOException;
import java.util.*;


@Data
public class UserInfoModel {
    private Map<String, Double> currencies;
    private List<String> selectedVendors;
    private String selectedCurrency;
    private String defaultCurrency;
    private Map<String, List> vendorCurrencies;
    private String exchangeRateDate;

    public UserInfoModel() {
        init();
    }

    private void init() {
        currencies = new HashMap<>();
        currencies.put("KRW", 1338.0);
        currencies.put("MXN", 17.06);
        currencies.put("IDR", 152.91);
        currencies.put("USD", 1.0);
        currencies.put("VND", 26760.0);
        currencies.put("CNY", 7.3);

        selectedVendors = new ArrayList<>();
        selectedVendors.add("AWS");

        selectedCurrency = "USD";
        defaultCurrency = "USD";

        vendorCurrencies = new HashMap<>();
        vendorCurrencies.put("aws", new ArrayList<>());
        vendorCurrencies.put("azure", null);
        vendorCurrencies.put("gcp", null);
        vendorCurrencies.put("ali", null);
        vendorCurrencies.put("oci", null);
        vendorCurrencies.put("ncp", null);

        exchangeRateDate = "2023-08-24";
    }
}
