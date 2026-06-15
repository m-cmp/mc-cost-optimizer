package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * 상품 청구 비용 목록
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-productdemandcostlist">ProductDemandCostList</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductDemandCostList {

    /**
     * ProductDemandCost 개수. ex) 15
     */
    private Integer totalRows;

    /**
     * {@link ProductDemandCost} 리스트 데이터 타입.
     */
    private List<ProductDemandCost> productDemandCostList;
}
