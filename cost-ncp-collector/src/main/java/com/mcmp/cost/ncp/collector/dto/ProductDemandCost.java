package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

/**
 * 상품 청구 비용
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-productdemandcost">ProductDemandCost</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductDemandCost {

    /**
     * 회원 번호. ex) 0000000
     */
    private String memberNo;

    /**
     * 청구 월. ex) 202508
     */
    private String demandMonth;

    /**
     * {@link ProductDemandType} 상품 청구 유형.
     */
    private ProductDemandType productDemandType;

    /**
     * 약정 할인 금액. ex) 0
     */
    private Double promiseDiscountAmount;

    /**
     * 프로모션 할인 금액. ex) 0
     */
    private Double promotionDiscountAmount;

    /**
     * 기타 할인 금액. ex) 0
     */
    private Double etcDiscountAmount;

    /**
     * 상품 할인 금액. ex) 0
     */
    private Double productDiscountAmount;

    /**
     * 크레딧 할인 금액. ex) 0
     */
    private Double creditDiscountAmount;

    /**
     * 위약 금액. ex) 0
     */
    private Double defaultAmount;

    /**
     * 사용 금액. ex) 10000
     */
    private Double useAmount;

    /**
     * 청구 금액. ex) 10000
     */
    private Double demandAmount;

    /**
     * 작성 일시(YYYY-MM-DDThh:mm:ssZ). ex) 2025-09-11 08:02:40+0900
     */
    private Date writeDate;

    /**
     * 회원 요금제 할인 금액. ex) 0
     */
    private Double memberPriceDiscountAmount;

    /**
     * 회원 약정 요금제 할인 금액. ex) 0
     */
    private Double memberPromiseDiscountAddAmount;

    /**
     * {@link CommonCode} 결제 통화.
     */
    private CommonCode payCurrency;

    /**
     * 이번 달 적용 환율. ex) 1
     */
    private Double thisMonthAppliedExchangeRate;
}
