package com.mcmp.gcpcollector.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GcpBillingRawDto {

    private Long id;
    private LocalDateTime created;

    private String billingAccountId;
    private Double cost;
    private String costType;
    private String currency;
    private Double currencyConversionRate;
    private LocalDateTime exportTime;

    private String invoiceMonth;

    private String serviceId;
    private String serviceDescription;

    private String skuId;
    private String skuDescription;

    private String projectId;
    private String projectNumber;
    private String projectName;
    private String projectAncestryNumbers;

    private String location;
    private String locationCountry;
    private String locationRegion;
    private String locationZone;

    private LocalDateTime usageStartTime;
    private LocalDateTime usageEndTime;
    private Double usageAmount;
    private String usageUnit;
    private Double usageAmountInPricingUnits;
    private String usagePricingUnit;

    private String adjustmentInfoId;
    private String adjustmentInfoDescription;
    private String adjustmentInfoMode;
    private String adjustmentInfoType;

    private String labels;
    private String systemLabels;
    private String tags;

    // labels(sys.* JSON)에서 추출한 servicegroup_meta 매핑용 식별자 (수집 시 파싱하여 채움)
    private String cspInstanceid; // labels.sys_cspresourceid → servicegroup_meta.csp_instanceid (조인키)
    private String vmId;          // labels.sys_id
    private String mciId;         // labels.sys_infraid
    private String serviceCd;     // labels.sys_namespace (ns_id)
}
