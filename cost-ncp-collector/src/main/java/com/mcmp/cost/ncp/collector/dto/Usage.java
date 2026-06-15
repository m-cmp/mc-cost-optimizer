package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용량 정보.
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-usage">Usage</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class Usage {

    /**
     * {@link CommonCode} 미터링 구분.
     */
    private CommonCode meteringType;

    /**
     * 상품 품목 종류 사용 월.
     */
    private String useMonth;

    /**
     * 사용량.
     */
    private Double usageQuantity;

    /**
     * 미터링 구분 사용 단위.
     */
    private CommonCode unit;

    /**
     * 사용자 미터링 사용 단위.
     */
    private CommonCode userUnit;
}
