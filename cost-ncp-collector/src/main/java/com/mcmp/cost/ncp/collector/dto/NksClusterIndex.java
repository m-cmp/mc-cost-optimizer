package com.mcmp.cost.ncp.collector.dto;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * NKS 클러스터 목록을 과금 행과 맞춰 보기 위한 조회용 인덱스.
 *
 * <p>NKS 관리비(VNKS) 과금 행의 contractProductList[0].instanceNo 는 마스터 인스턴스 번호(예: 145810822)인데,
 * 클러스터 목록 API 의 instanceNo 는 0 으로 와서 instanceNo 로는 짝이 안 맞는다(2026-10-14 실응답, JPN).
 * 대신 과금 행의 contract.instanceName 과 클러스터 name 이 같으므로 이름으로도 찾을 수 있게 둔다.
 */
public record NksClusterIndex(Map<String, NksCluster> byInstanceNo, Map<String, NksCluster> byName) {

    public static final NksClusterIndex EMPTY = new NksClusterIndex(Map.of(), Map.of());

    /** uuid 가 없는 항목은 매칭해도 쓸 값이 없어 제외. instanceNo 0(미제공)은 instanceNo 맵에 넣지 않는다. */
    public static NksClusterIndex of(Collection<NksCluster> clusters) {
        if (clusters == null || clusters.isEmpty()) return EMPTY;
        Map<String, NksCluster> byInstanceNo = clusters.stream()
                .filter(k -> k.getUuid() != null && k.getInstanceNo() != null && k.getInstanceNo() != 0L)
                .collect(Collectors.toMap(k -> String.valueOf(k.getInstanceNo()), k -> k, (a, b) -> a));
        Map<String, NksCluster> byName = clusters.stream()
                .filter(k -> k.getUuid() != null && k.getName() != null && !k.getName().isBlank())
                .collect(Collectors.toMap(k -> k.getName().trim(), k -> k, (a, b) -> a));
        return new NksClusterIndex(byInstanceNo, byName);
    }

    public NksCluster findByInstanceNo(String instanceNo) {
        return instanceNo == null ? null : byInstanceNo.get(instanceNo);
    }

    public NksCluster findByName(String name) {
        return name == null ? null : byName.get(name.trim());
    }

    public int size() {
        return Math.max(byInstanceNo.size(), byName.size());
    }
}
