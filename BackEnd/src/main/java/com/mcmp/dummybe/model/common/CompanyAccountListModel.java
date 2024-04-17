package com.mcmp.dummybe.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyAccountListModel {
    private String accId;
    private String alias;
    @JsonProperty(value = "hlthYn")
    private String hlthYn;
}
