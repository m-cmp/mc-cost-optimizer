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
@Table(name = "ncp_cost_service_month")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NcpCostServiceMonth extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 회원 번호. ex) 0000000
     */
    @Column(name = "member_no", nullable = false)
    private String memberNo;

    /**
     * 상품 청구 유형 이름. ex) Virtual Private Cloud
     */
    @Column(name = "product_demand_type", nullable = false)
    private String productDemandType;

    /**
     * 	청구 월. ex) 202509
     */
    @Column(name = "demand_month", nullable = false)
    private String demandMonth;

    /**
     * 	사용 금액. ex) 2580.0
     */
    @Column(name = "use_amount", nullable = false)
    private Double useAmount;

    /**
     * 청구 금액. ex) 2580.0
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
    public NcpCostServiceMonth(Long id, String memberNo, String productDemandType, String demandMonth, Double useAmount, Double demandAmount, Date writeDate, String payCurrency) {
        this.id = id;
        this.memberNo = memberNo;
        this.productDemandType = productDemandType;
        this.demandMonth = demandMonth;
        this.useAmount = useAmount;
        this.demandAmount = demandAmount;
        this.writeDate = writeDate;
        this.payCurrency = payCurrency;
    }
}
