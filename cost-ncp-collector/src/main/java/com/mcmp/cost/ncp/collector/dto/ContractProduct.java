package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

/**
 * 계약 상품
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-contractproduct">ContractProduct</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class ContractProduct {

    /**
     * 계약 상품 일련 번호. ex) 1
     */
    private String contractProductSequence;

    /**
     * 이전 계약 상품 일련 번호. ex) "
     */
    private String beforeContractProductSequence;

    /**
     * 상품 코드.
     */
    private String productCode;

    /**
     * 요금제 번호.
     */
    private String priceNo;

    /**
     * 약정 번호.
     */
    private String promiseNo;

    /**
     * 인스턴스 번호.
     */
    private String instanceNo;

    /**
     * {@link CommonCode} 상품 품목 종류.
     */
    private CommonCode productItemKind;

    /**
     * {@link CommonCode} 과금 유형 코드.
     */
    private CommonCode productRatingType;

    /**
     * {@link CommonCode} 서비스 상태.
     */
    private CommonCode serviceStatus;

    /**
     * 서비스 시작 일시.
     */
    private Date serviceStartDate;

    /**
     * 서비스 종료 일시.
     */
    private Date serviceEndDate;

    /**
     * 상품 크기.
     */
    private Long productSize;

    /**
     * 상품 수.
     */
    private Integer productCount;

    // {@link Usage} 사용량 목록.
    // 필드는 정의되어있으나, API 호출시 별 다른 값이 없다. 따라서 해당 필드는 주석처리한다.
    // private List<Usage> usageList;
}
