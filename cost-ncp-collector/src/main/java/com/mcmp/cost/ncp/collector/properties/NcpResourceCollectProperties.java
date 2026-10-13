package com.mcmp.cost.ncp.collector.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 비-VM 자원(NKS 클러스터, Object Storage) 수집 스펙.
 * NCP 과금 코드값은 공식 문서에 열거되어 있지 않아(getCostRelationCodeList 로만 확인) env 로 외부화한다.
 * 코드가 비어 있으면 해당 유형은 WARN 로그 후 skip 된다.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ncp.resource-collect")
public class NcpResourceCollectProperties {

    /** 유형별 수집 스펙 (application.yml ncp.resource-collect.types) */
    private List<TypeSpec> types = new ArrayList<>();

    @Getter
    @Setter
    public static class TypeSpec {
        /** 저장용 자원 유형. K8S | OBJECT_STORAGE (servicegroup_meta.service_type 과 철자 동일) */
        private String resourceType;
        /** getContractDemandCostList 의 demandTypeCode 필터(상위 코드, 권장). ex) OSSM (Object Storage 청구 유형) */
        private List<String> demandTypeCodes = new ArrayList<>();
        /** 비어있지 않으면 demandTypeDetailCode 로 필터(상세 코드). */
        private List<String> demandTypeDetailCodes = new ArrayList<>();
        /** true 면 NKS 클러스터 목록 API 로 instanceNo → uuid 를 해석해 resource_id 로 저장 (K8S 만) */
        private boolean resolveNksUuid = false;
    }
}
