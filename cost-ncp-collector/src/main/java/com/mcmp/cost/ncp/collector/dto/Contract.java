package com.mcmp.cost.ncp.collector.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

/**
 * 계약 정보
 * <a href="https://api.ncloud-docs.com/docs/common-vapidatatype-contract">Contract</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class Contract {

    /**
     * 회원 번호. ex) 0000000
     */
    private String memberNo;

    /**
     * 계약 번호. ex) 12345678
     */
    private String contractNo;

    /**
     * 연관 계약 번호. ex) ""
     */
    private String conjunctionContractNo;

    /**
     * {@link CommonCode} 계약 구분.
     */
    private CommonCode contractType;

    /**
     * {@link CommonCode} 계약 상태.
     */
    private CommonCode contractStatus;

    /**
     * 계약 시작 일시 ex) 2023-11-08T13:35:28+0900
     */
    private Date contractStartDate;

    /**
     * 계약 종료 일시 ex) 2999-12-31T23:59:59+0900
     */
    private Date contractEndDate;

    /**
     * 인스턴스 이름 ex) dongwoo-abc-abc-abc
     */
    private String instanceName;

    /**
     * 리전 코드 ex) KR
     */
    private String regionCode;

    /**
     * {@link CommonCode} 플랫폼 구분.
     */
    private CommonCode platformType;

    /**
     * {@link ContractProduct} 계약 상품 목록.
     */
    private List<ContractProduct> contractProductList;
}
