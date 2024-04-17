package com.mcmp.dummybe.model.common;

import lombok.Data;

import java.util.List;

@Data
public class CompanyVendorModel {
    private String cloudVndrId;
    private String cloudVndrNm;
    private List<CompanyAccountListModel> accountList;
}
