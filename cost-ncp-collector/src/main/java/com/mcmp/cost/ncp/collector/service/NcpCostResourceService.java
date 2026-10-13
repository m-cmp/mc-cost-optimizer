package com.mcmp.cost.ncp.collector.service;

import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostResourceMonth;
import java.util.List;

public interface NcpCostResourceService {

    /**
     * NKS 클러스터 / Object Storage 계약별 이 달의 월 누적 청구 비용을 조회해 ncp_cost_resource_month 에 적재(INSERT IGNORE)하고 반환한다.
     * VM 경로(getCostByVm)와 달리 vserver 상세조회를 하지 않으며, 코드값 미설정 유형은 WARN 후 skip 한다.
     *
     * @param ncpApiCredentialDto {@link NcpApiCredentialDto}
     * @return 적재 대상 행 (중복 포함)
     */
    List<NcpCostResourceMonth> getCostByResource(NcpApiCredentialDto ncpApiCredentialDto);
}
