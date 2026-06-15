package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

/**
 * 계약 청구 비용.
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-contractdemandcost">ContractDemandCost</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class ContractDemandCost {

    /**
     * 회원 번호. ex) 0000000
     */
    private String memberNo;

    /**
     * 리전 코드. ex) KR
     */
    private String regionCode;

    /**
     * {@link CommonCode} 청구 유형 코드.
     */
    private CommonCode demandType;

    /**
     * {@link CommonCode} 청구 유형 상세 코드.
     */
    private CommonCode demandTypeDetail;

    /**
     * {@link Contract} 계약 정보.
     */
    private Contract contract;

    /**
     * 청구 월. ex) 202509
     */
    private String demandMonth;

    /**
     * 단위 사용량. ex) 240
     */
    private Double unitUsageQuantity;

    /**
     * 패키지 단위 사용량. ex) 0
     */
    private Double packageUnitUsageQuantity;

    /**
     * 총 단위 사용량. ex) 240
     */
    private Double totalUnitUsageQuantity;

    /**
     * {@link CommonCode} 사용량 단위.
     */
    private CommonCode usageUnit;

    /**
     * 상품 가격. ex) 0.08
     */
    private Double productPrice;

    /**
     * 사용 금액. ex) 1920
     */
    private Double useAmount;

    /**
     * 프로모션 할인 금액. ex) 0
     */
    private Double promotionDiscountAmount;

    /**
     * 기타 할인 금액. ex) 0
     */
    private Double etcDiscountAmount;

    /**
     * 약정 할인 금액. ex) 0
     */
    private Double promiseDiscountAmount;

    /**
     * 청구 금액. ex) 1920
     */
    private Double demandAmount;

    /**
     * 작성 일시.(YYYY-MM-DDThh:mm:ssZ) ex) 2025-09-11T08:02:40+0900
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
