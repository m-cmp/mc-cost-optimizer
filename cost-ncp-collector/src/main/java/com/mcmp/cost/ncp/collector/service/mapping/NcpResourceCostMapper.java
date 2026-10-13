package com.mcmp.cost.ncp.collector.service.mapping;

import com.mcmp.cost.ncp.collector.dto.CommonCode;
import com.mcmp.cost.ncp.collector.dto.Contract;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCost;
import com.mcmp.cost.ncp.collector.dto.ContractProduct;
import com.mcmp.cost.ncp.collector.dto.NksCluster;
import com.mcmp.cost.ncp.collector.entity.NcpCostResourceMonth;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * ContractDemandCost → NcpCostResourceMonth 순수 매핑 (vserver 상세조회 없음).
 *
 * <p>resource_id 결정 순서: K8S 는 NKS 목록의 uuid(NKS_UUID) → 과거 적재 행에서 uuid 복원(NKS_UUID) → instanceNo(INSTANCE_NO) → contractNo(CONTRACT_NO).
 * OBJECT_STORAGE 는 계정 단위 과금(OSSM)이라 계약 정보가 없는 행이 오므로, 그때는 계정 번호(MEMBER_NO)를 resource_id 로 쓴다.
 * Tumblebug 은 NKS 클러스터를 uuid 로 알고 있어서 NKS_UUID 만 servicegroup_meta 와 1:1 매칭된다.
 */
public final class NcpResourceCostMapper {

    public enum IdSource { NKS_UUID, INSTANCE_NO, CONTRACT_NO, MEMBER_NO }

    private NcpResourceCostMapper() {}

    public static NcpCostResourceMonth toEntity(ContractDemandCost c, String resourceType,
                                                Map<String, NksCluster> nksByInstanceNo,
                                                Function<String, String> fallbackUuidLookup) {
        Contract contract = c.getContract();
        ContractProduct product = (contract != null && contract.getContractProductList() != null && !contract.getContractProductList().isEmpty())
                ? contract.getContractProductList().getFirst() : null;
        String instanceNo   = product != null ? trimToNull(product.getInstanceNo()) : null;
        String contractNo   = contract != null ? trimToNull(contract.getContractNo()) : null;
        String instanceName = contract != null ? trimToNull(contract.getInstanceName()) : null;

        String resourceId;
        String resourceName;
        IdSource source;
        if ("K8S".equals(resourceType) && instanceNo != null && nksByInstanceNo != null && nksByInstanceNo.containsKey(instanceNo)) {
            NksCluster k = nksByInstanceNo.get(instanceNo);
            resourceId = k.getUuid();
            resourceName = k.getName();
            source = IdSource.NKS_UUID;
        } else if ("K8S".equals(resourceType) && instanceNo != null && fallbackUuidLookup != null && fallbackUuidLookup.apply(instanceNo) != null) {
            resourceId = fallbackUuidLookup.apply(instanceNo);   // 삭제된 클러스터: 과거 행에서 복원
            resourceName = instanceName;
            source = IdSource.NKS_UUID;
        } else if (instanceNo != null) {
            resourceId = instanceNo;
            resourceName = instanceName;
            source = IdSource.INSTANCE_NO;
        } else if (contractNo != null) {
            resourceId = contractNo;
            resourceName = instanceName;
            source = IdSource.CONTRACT_NO;
        } else if ("OBJECT_STORAGE".equals(resourceType) && trimToNull(c.getMemberNo()) != null) {
            // 2026-10-09 실응답: OSSM 행은 contract 의 계약·인스턴스 번호가 비어 있고 청구 상세(OSSZ, OSAM1 …)별로만 나뉜다 → 계정 단위 1자원
            resourceId = c.getMemberNo().trim();
            resourceName = instanceName;
            source = IdSource.MEMBER_NO;
        } else {
            throw new IllegalArgumentException("resource_id 를 결정할 수 없습니다 (instanceNo/contractNo 모두 없음): memberNo="
                    + c.getMemberNo() + ", demandMonth=" + c.getDemandMonth());
        }

        Date writeDate = c.getWriteDate();
        if (writeDate == null) {
            throw new IllegalArgumentException("writeDate 가 없습니다: memberNo=" + c.getMemberNo() + ", resourceId=" + resourceId);
        }

        return NcpCostResourceMonth.builder()
                .memberNo(c.getMemberNo())
                .demandMonth(c.getDemandMonth())
                .regionCode(nvl(trimToNull(c.getRegionCode()), contract != null ? trimToNull(contract.getRegionCode()) : null, "KR"))
                .resourceType(resourceType)
                .demandTypeCode(nvl(code(c.getDemandType()), ""))
                .demandTypeName(codeName(c.getDemandType()))
                .demandTypeDetailCode(nvl(code(c.getDemandTypeDetail()), ""))
                .demandTypeDetailName(codeName(c.getDemandTypeDetail()))
                .contractNo(nvl(contractNo, ""))
                .productCode(product != null ? product.getProductCode() : null)
                .productName(product != null ? codeName(product.getProductItemKind()) : null)
                .instanceNo(instanceNo)
                .instanceName(instanceName)
                .resourceId(resourceId)
                .resourceName(resourceName)
                .resourceIdSource(source.name())
                .usageUnitCode(nvl(code(c.getUsageUnit()), ""))
                .usageUnitName(nvl(codeName(c.getUsageUnit()), ""))
                .productPrice(nvl(c.getProductPrice(), 0d))
                .unitUsageQuantity(nvl(c.getUnitUsageQuantity(), 0d))
                .totalUnitUsageQuantity(nvl(c.getTotalUnitUsageQuantity(), 0d))
                .useAmount(nvl(c.getUseAmount(), 0d))
                .demandAmount(nvl(c.getDemandAmount(), 0d))
                .writeDate(writeDate)
                .snapshotDate(writeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .payCurrency(c.getPayCurrency() != null && c.getPayCurrency().getCode() != null ? c.getPayCurrency().getCode() : "KRW")
                .build();
    }

    static String code(CommonCode cc) { return cc == null ? null : trimToNull(cc.getCode()); }
    static String codeName(CommonCode cc) { return cc == null ? null : trimToNull(cc.getCodeName()); }
    static String trimToNull(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
    @SafeVarargs
    static <T> T nvl(T... values) {
        for (T v : values) if (v != null) return v;
        return null;
    }
}
