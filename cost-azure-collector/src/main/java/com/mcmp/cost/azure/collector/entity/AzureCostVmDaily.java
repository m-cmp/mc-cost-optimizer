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
@Table(name = "azure_cost_vm_daily")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AzureCostVmDaily extends AuditEntity {

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
     * 리소스 그룹. ex) rg-dongwoo-1
     */
    @Column(name = "resource_group_name", nullable = false)
    private String resourceGroupName;

    /**
     * 리소스 아이디.(AWS의 ARN과 비슷)
     * ex) "/subscriptions/00000000-0000-0000-0000-00000000000/resourcegroups/rg-dongwoo-1/providers/microsoft.compute/virtualmachines/vm-1"
     */
    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    /**
     * region 명. ex) koreacentral
     */
    @Column(name = "region", nullable = false)
    private String region;

    /**
     * 인스턴스 타입. ex) Standard_DS3_v2
     */
    @Column(name = "instance_type", nullable = false)
    private String instanceType;

    /**
     * 인스턴스 os 타입. ex) WINDOWS
     */
    @Column(name = "os_type", nullable = false)
    private String osType;

    /**
     * vm 아이디 ex) vm-1
     */
    @Column(name = "vm_id", nullable = false)
    private String vmId;

    /**
     * 리소스 고유 아이디. ex) 00000000-0000-0000-0000-00000000000
     */
    @Column(name = "resource_guid", nullable = false)
    private String resourceGuid;

    /**
     * 통화 단위. ex) KRW
     */
    @Column(name = "currency", nullable = false)
    private String currency;

    @Builder
    public AzureCostVmDaily(Long id, String tenantId, String subscriptionId, Double preTaxCost, String usageDate, String resourceGroupName, String resourceId, String region, String instanceType, String osType, String vmId, String resourceGuid, String currency) {
        this.id = id;
        this.tenantId = tenantId;
        this.subscriptionId = subscriptionId;
        this.preTaxCost = preTaxCost;
        this.usageDate = usageDate;
        this.resourceGroupName = resourceGroupName;
        this.resourceId = resourceId;
        this.region = region;
        this.instanceType = instanceType;
        this.osType = osType;
        this.vmId = vmId;
        this.resourceGuid = resourceGuid;
        this.currency = currency;
    }
}
