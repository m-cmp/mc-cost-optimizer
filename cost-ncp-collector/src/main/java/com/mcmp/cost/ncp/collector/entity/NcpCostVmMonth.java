package com.mcmp.cost.ncp.collector.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "ncp_cost_vm_month")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NcpCostVmMonth extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 번호. ex) 0000000
     */
    @Column(name = "member_no", nullable = false)
    private String memberNo;

    /**
     * 청구 월. ex) 202509
     */
    @Column(name = "demand_month", nullable = false)
    private String demandMonth;

    /**
     * 리전 코드. ex) KR
     */
    @Column(name = "region_code", nullable = false)
    private String regionCode;

    /**
     * 서버 스펙 코드. ex) s2-g2-s50
     */
    @Column(name = "server_spec_code", nullable = false)
    private String serverSpecCode;

    /**
     * 인스턴스 번호. ex) 00000000
     */
    @Column(name = "instance_no", nullable = false)
    private String instanceNo;

    /**
     * 인스턴스 이름. ex) dongwoo-abc-abc-abc
     */
    @Column(name = "instance_name", nullable = false)
    private String instanceName;

    /**
     * 사용량 단위 코드. ex) USAGE_HH
     */
    @Column(name = "usage_unit_code", nullable = false)
    private String usageUnitCode;

    /**
     * 사용량 단위 이름. ex) Usage time (per hour)
     */
    @Column(name = "usage_unit_name", nullable = false)
    private String usageUnitName;

    /**
     * 상품 가격. ex) 123
     */
    @Column(name = "product_price", nullable = false)
    private Double productPrice;

    /**
     * 단위 사용량. ex) 240
     */
    @Column(name = "unit_usage_quantity", nullable = false)
    private Double unitUsageQuantity;

    /**
     * 총 단위 사용량. ex) 240
     */
    @Column(name = "total_unit_usage_quantity", nullable = false)
    private Double totalUnitUsageQuantity;

    /**
     * 사용 금액. ex) 29520
     */
    @Column(name = "use_amount", nullable = false)
    private Double useAmount;

    /**
     * 청구 금액. ex) 29520
     */
    @Column(name = "demand_amount", nullable = false)
    private Double demandAmount;

    /**
     * 작성 일시(YYYY-MM-DDThh:mm:ssZ). ex) 2025-09-11 08:02:40+0900
     */
    @Column(name = "write_date", nullable = false)
    private Date writeDate;

    /**
     * 결제 통화. ex) KRW
     */
    @Column(name = "pay_currency", nullable = false)
    private String payCurrency;

    @Builder
    public NcpCostVmMonth(Long id, String memberNo, String demandMonth, String serverSpecCode, String regionCode, String instanceNo, String instanceName, String usageUnitCode, String usageUnitName, Double productPrice, Double unitUsageQuantity, Double totalUnitUsageQuantity, Double useAmount, Double demandAmount, Date writeDate, String payCurrency) {
        this.id = id;
        this.memberNo = memberNo;
        this.demandMonth = demandMonth;
        this.serverSpecCode = serverSpecCode;
        this.regionCode = regionCode;
        this.instanceNo = instanceNo;
        this.instanceName = instanceName;
        this.usageUnitCode = usageUnitCode;
        this.usageUnitName = usageUnitName;
        this.productPrice = productPrice;
        this.unitUsageQuantity = unitUsageQuantity;
        this.totalUnitUsageQuantity = totalUnitUsageQuantity;
        this.useAmount = useAmount;
        this.demandAmount = demandAmount;
        this.writeDate = writeDate;
        this.payCurrency = payCurrency;
    }
}
