package com.mcmp.cost.azure.collector.entity;

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

@Entity
@Getter
@Setter
@Table(name = "azure_cost_service_daily")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AzureCostServiceDaily extends AuditEntity {

    /**
     * Entity ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    /**
     * subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    @Column(name = "subscription_id", nullable = false)
    private String subscriptionId;

    /**
     * 비용. ex) 16345.824
     */
    @Column(name = "pre_tax_cost", nullable = false)
    private Double preTaxCost;

    /**
     * 날짜. ex) 20250903
     */
    @Column(name = "usage_date", nullable = false)
    private String usageDate;

    /**
     * 서비스 이름. ex) Virtual Machines
     */
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    /**
     * 통화 단위. ex) KRW
     */
    @Column(name = "currency", nullable = false)
    private String currency;

    @Builder
    public AzureCostServiceDaily(Long id, String tenantId, String subscriptionId, Double preTaxCost, String usageDate, String serviceName, String currency) {
        this.id = id;
        this.tenantId = tenantId;
        this.subscriptionId = subscriptionId;
        this.preTaxCost = preTaxCost;
        this.usageDate = usageDate;
        this.serviceName = serviceName;
        this.currency = currency;
    }
}
