package com.mcmp.cost.ncp.collector.service;

import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import java.util.List;

public interface NcpCostMonthService {

    /**
     * 설정 기간에 따라 서비스별 청구 비용 목록을 조회한다. </br>
     * NCP는 서비스 일별 청구비용을 API를 제공하지 않고 월별만 제공한다.
     * 하지만, 월별 청구 비용이 매일 8시에 합산되어 증분되므로 이를 계산하여 매일 비용 증분을 게산 할 수 있다.</br>
     * <p>
     * 조회 하는 API의 공식 문서는 다음과 같다.
     * <a href="https://api.ncloud-docs.com/docs/platform-costandusage-getproductdemandcostlist">getProductDemandCostList</a>
     *
     * @param ncpApiCredentialDto {@link NcpApiCredentialDto}
     * @return {@link NcpCostServiceMonth} 리스트 데이터 타입.
     */
    List<NcpCostServiceMonth> getCostByService(NcpApiCredentialDto ncpApiCredentialDto);

    /**
     * 설정 기간에 따라 VM별 청구 비용 목록을 조회한다. </br>
     * NCP는 VM별 일별 청구비용을 API를 제공하지 않고 월별만 제공한다.
     * 하지만, 월별 청구 비용이 매일 8시에 합산되어 증분되므로 이를 계산하여 매일 비용 증분을 게산 할 수 있다.</br>
     * <p>
     * 조회 하는 API의 공식 문서는 다음과 같다.
     * <a href="https://api.ncloud-docs.com/docs/platform-costandusage-getcontractdemandcostlist">getContractDemandCostList</a>
     *
     * @param ncpApiCredentialDto {@link NcpApiCredentialDto}
     * @return {@link NcpCostVmMonth} 리스트 데이터 타입.
     */
    List<NcpCostVmMonth> getCostByVm(NcpApiCredentialDto ncpApiCredentialDto);
}
