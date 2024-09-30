package com.mcmp.costselector.unused.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InstOptiRcmdRst {
    private LocalDate createDT;
    private String resourceID;
    private String cspType;
    private String cspAccount;
    private String originType;
    private String rcmdType;
    private String planType;
    private Double originUSD;
    private Double rcmdUSD;
}
