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

import java.time.LocalDate;
import java.util.Date;

/**
 * NCP 자원(K8S / OBJECT_STORAGE) 계약별 월 누적 청구 비용 — ncp_cost_vm_month 의 자매 테이블(server_spec_code 없음).
 * resource_id 가 servicegroup_meta.csp_instanceid 조인 키(K8S = NKS 클러스터 UUID). DDL: ddl_NcpCostResourceMonth.sql
 * 적재는 JPA 가 아니라 MyBatis INSERT IGNORE(UNIQUE 키 dedupe) 로 한다 — {@link com.mcmp.cost.ncp.collector.mapper.NcpCostResourceDailyMapper}.
 */
@Getter
@Setter
@Entity
@Table(name = "ncp_cost_resource_month")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NcpCostResourceMonth extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 회원 번호. ex) 0000000 */
    @Column(name = "member_no", nullable = false, length = 100)
    private String memberNo;

    /** 청구 월. ex) 202510 */
    @Column(name = "demand_month", nullable = false, length = 6)
    private String demandMonth;

    /** 리전 코드. ex) KR */
    @Column(name = "region_code", nullable = false, length = 10)
    private String regionCode;

    /** 자원 유형. K8S | OBJECT_STORAGE */
    @Column(name = "resource_type", nullable = false, length = 20)
    private String resourceType;

    /** 청구 유형 코드. ex) OSSM */
    @Column(name = "demand_type_code", nullable = false, length = 20)
    private String demandTypeCode;

    /** 청구 유형 이름. ex) Object Storage */
    @Column(name = "demand_type_name", length = 100)
    private String demandTypeName;

    /** 청구 유형 상세 코드 (null 이면 '' — UNIQUE 키 구성요소) */
    @Column(name = "demand_type_detail_code", nullable = false, length = 20)
    private String demandTypeDetailCode;

    /** 청구 유형 상세 이름 */
    @Column(name = "demand_type_detail_name", length = 100)
    private String demandTypeDetailName;

    /** 계약 번호 (null 이면 '' — UNIQUE 키 구성요소) */
    @Column(name = "contract_no", nullable = false, length = 50)
    private String contractNo;

    /** 상품 코드 (contractProductList[0].productCode) */
    @Column(name = "product_code", length = 50)
    private String productCode;

    /** 상품 이름 (contractProductList[0].productItemKind.codeName) */
    @Column(name = "product_name", length = 200)
    private String productName;

    /** NCP 인스턴스 번호(원본). ex) 23320000 */
    @Column(name = "instance_no", length = 100)
    private String instanceNo;

    /** NCP 인스턴스 이름(원본, contract.instanceName) */
    @Column(name = "instance_name", length = 200)
    private String instanceName;

    /** servicegroup_meta.csp_instanceid 조인 키. K8S = NKS 클러스터 UUID, OBJECT_STORAGE = instance_no(계정 단위) */
    @Column(name = "resource_id", nullable = false, length = 200)
    private String resourceId;

    /** 자원 표시 이름. K8S = 클러스터명, OBJECT_STORAGE = instance_name */
    @Column(name = "resource_name", length = 200)
    private String resourceName;

    /** resource_id 출처. NKS_UUID | INSTANCE_NO | CONTRACT_NO */
    @Column(name = "resource_id_source", nullable = false, length = 20)
    private String resourceIdSource;

    /** 사용량 단위 코드. ex) USAGE_HH */
    @Column(name = "usage_unit_code", nullable = false, length = 50)
    private String usageUnitCode;

    /** 사용량 단위 이름 */
    @Column(name = "usage_unit_name", nullable = false, length = 100)
    private String usageUnitName;

    /** 상품 가격 */
    @Column(name = "product_price", nullable = false)
    private Double productPrice;

    /** 단위 사용량 */
    @Column(name = "unit_usage_quantity", nullable = false)
    private Double unitUsageQuantity;

    /** 총 단위 사용량 */
    @Column(name = "total_unit_usage_quantity", nullable = false)
    private Double totalUnitUsageQuantity;

    /** 사용 금액(월 누적, KRW) */
    @Column(name = "use_amount", nullable = false)
    private Double useAmount;

    /** 청구 금액(월 누적, KRW) */
    @Column(name = "demand_amount", nullable = false)
    private Double demandAmount;

    /** NCP 작성 일시. ex) 2025-09-11 08:02:40 */
    @Column(name = "write_date", nullable = false)
    private Date writeDate;

    /** DATE(write_date). 재실행 dedupe 키 */
    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    /** 결제 통화. ex) KRW */
    @Column(name = "pay_currency", nullable = false, length = 10)
    private String payCurrency;

    @Builder
    public NcpCostResourceMonth(Long id, String memberNo, String demandMonth, String regionCode, String resourceType,
                                String demandTypeCode, String demandTypeName, String demandTypeDetailCode, String demandTypeDetailName,
                                String contractNo, String productCode, String productName, String instanceNo, String instanceName,
                                String resourceId, String resourceName, String resourceIdSource,
                                String usageUnitCode, String usageUnitName, Double productPrice, Double unitUsageQuantity,
                                Double totalUnitUsageQuantity, Double useAmount, Double demandAmount, Date writeDate,
                                LocalDate snapshotDate, String payCurrency) {
        this.id = id;
        this.memberNo = memberNo;
        this.demandMonth = demandMonth;
        this.regionCode = regionCode;
        this.resourceType = resourceType;
        this.demandTypeCode = demandTypeCode;
        this.demandTypeName = demandTypeName;
        this.demandTypeDetailCode = demandTypeDetailCode;
        this.demandTypeDetailName = demandTypeDetailName;
        this.contractNo = contractNo;
        this.productCode = productCode;
        this.productName = productName;
        this.instanceNo = instanceNo;
        this.instanceName = instanceName;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceIdSource = resourceIdSource;
        this.usageUnitCode = usageUnitCode;
        this.usageUnitName = usageUnitName;
        this.productPrice = productPrice;
        this.unitUsageQuantity = unitUsageQuantity;
        this.totalUnitUsageQuantity = totalUnitUsageQuantity;
        this.useAmount = useAmount;
        this.demandAmount = demandAmount;
        this.writeDate = writeDate;
        this.snapshotDate = snapshotDate;
        this.payCurrency = payCurrency;
    }
}
