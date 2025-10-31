package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * 계약 청구 비용 목록.
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-contractdemandcostlist">ContractDemandCostList</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class ContractDemandCostList {

    /**
     * 조회된 목록의 총 개수. ex) 40
     */
    private Integer totalRows;

    /**
     * {@link ContractDemandCost} 리스트 데이터 타입.
     */
    private List<ContractDemandCost> contractDemandCostList;
}
