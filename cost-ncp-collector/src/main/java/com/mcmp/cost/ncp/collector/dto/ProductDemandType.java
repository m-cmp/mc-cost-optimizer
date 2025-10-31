package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상품 청구 유형
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-productdemandtype">ProductDemandType</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductDemandType {

    /**
     * 코드. ex) BST
     */
    private String code;

    /**
     * 코드 이름. ex) Block Storage
     */
    private String codeName;

    /**
     * 리전 코드. ex) ""
     */
    private String regionCode;
}
