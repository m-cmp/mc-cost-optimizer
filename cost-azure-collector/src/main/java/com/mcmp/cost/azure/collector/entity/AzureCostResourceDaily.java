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

/**
 * Azure 비-VM 자원(AKS managed cluster, Storage Account) 별 일별 요금.
 * azure_cost_vm_daily 의 자매 테이블로, VM 전용 컬럼(instance_type/os_type/vm_id/resource_guid) 이 없고
 * resource_type(K8S | K8S_NODE | OBJECT_STORAGE) 으로 자원 종류를 구분한다. DDL: ddl_AzureCostResourceDaily.sql
 */
@Entity
@Getter
@Setter
@Table(name = "azure_cost_resource_daily")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AzureCostResourceDaily extends AuditEntity {

    /** Entity ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 테넌트 아이디. ex) 00000000-0000-0000-0000-00000000000 */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    /** subscriptions 아이디. ex) 00000000-0000-0000-0000-00000000000 */
    @Column(name = "subscription_id", nullable = false, length = 36)
    private String subscriptionId;

    /** 비용(자원·일 합계, KRW). ex) 16345.824 */
    @Column(name = "pre_tax_cost", nullable = false)
    private Double preTaxCost;

    /** 날짜(yyyyMMdd). ex) 20250903 */
    @Column(name = "usage_date", nullable = false, length = 8)
    private String usageDate;

    /** 리소스 그룹. ex) rg-dongwoo-1 */
    @Column(name = "resource_group_name", nullable = false)
    private String resourceGroupName;

    /**
     * ARM 리소스 아이디. Cost Management 가 반환한 소문자 원문 그대로 저장한다.
     * ex) /subscriptions/.../resourcegroups/rg-1/providers/microsoft.containerservice/managedclusters/aks-1
     * BackEnd 는 LOWER(servicegroup_meta.csp_instanceid) 와 비교한다.
     */
    @Column(name = "resource_id", nullable = false, length = 512)
    private String resourceId;

    /** resource_id 마지막 세그먼트(클러스터명 / 스토리지 계정명). ex) aks-1 */
    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    /** 자원 유형. K8S | K8S_NODE | OBJECT_STORAGE ({@link com.mcmp.cost.azure.collector.utils.AzureResourceType}) */
    @Column(name = "resource_type", nullable = false, length = 20)
    private String resourceType;

    /** Azure 서비스명. ex) Azure Kubernetes Service, Storage */
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    /** ResourceLocation. ex) koreacentral (nullable) */
    @Column(name = "region")
    private String region;

    /** 통화 단위. ex) KRW */
    @Column(name = "currency", nullable = false)
    private String currency;

    @Builder
    public AzureCostResourceDaily(Long id, String tenantId, String subscriptionId, Double preTaxCost, String usageDate,
                                  String resourceGroupName, String resourceId, String resourceName, String resourceType,
                                  String serviceName, String region, String currency) {
        this.id = id;
        this.tenantId = tenantId;
        this.subscriptionId = subscriptionId;
        this.preTaxCost = preTaxCost;
        this.usageDate = usageDate;
        this.resourceGroupName = resourceGroupName;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.serviceName = serviceName;
        this.region = region;
        this.currency = currency;
    }
}
