package com.mcmp.cost.ncp.collector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContractDemandCostListWrapper {

    /**
     * API 응답 Wrapper.
     */
    @JsonProperty("getContractDemandCostListResponse")
    private ContractDemandCostList getContractDemandCostListResponse;
}
