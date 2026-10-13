package com.mcmp.cost.azure.collector.utils;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 비-VM 수집 대상 자원 유형. Cost Management 의 ResourceType dimension 값(소문자 ARM 타입)과
 * 저장용 대표 ServiceName, 그리고 ResourceId 경로 기반 분류를 한 곳에서 정의한다.
 *
 * <p>왜 ServiceName 이 아니라 ResourceType 으로 거르나: Managed Disk/Snapshot 도 ServiceName="Storage" 라 섞여 들어오고,
 * Storage Account 의 egress 는 ServiceName="Bandwidth" 로 빠지기 때문이다.
 */
@Getter
public enum AzureResourceType {
    K8S("microsoft.containerservice/managedclusters", "Azure Kubernetes Service"),
    /** AKS 워커 노드. 노드 VM 은 스케일셋 단위로 청구된다. AKS 가 아닌 스케일셋도 들어오지만 BackEnd 는 메타에 있는 것만 집계 */
    K8S_NODE("microsoft.compute/virtualmachinescalesets", "Virtual Machines"),
    OBJECT_STORAGE("microsoft.storage/storageaccounts", "Storage");

    /** Cost Management ResourceType dimension 값(소문자) */
    private final String armType;
    /** 저장용 대표 ServiceName */
    private final String serviceName;

    AzureResourceType(String armType, String serviceName) {
        this.armType = armType;
        this.serviceName = serviceName;
    }

    public static List<String> armTypes() {
        return Arrays.stream(values()).map(AzureResourceType::getArmType).toList();
    }

    /** ResourceId 경로( /providers/{armType}/ )로 분류. 대상 외 유형이면 empty. 대소문자 무관. */
    public static Optional<AzureResourceType> fromResourceId(String resourceId) {
        if (resourceId == null) return Optional.empty();
        String lower = resourceId.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(t -> lower.contains("/providers/" + t.armType + "/"))
                .findFirst();
    }
}
