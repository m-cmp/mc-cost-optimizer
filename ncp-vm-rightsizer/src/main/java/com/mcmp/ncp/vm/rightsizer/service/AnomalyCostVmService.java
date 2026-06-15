package com.mcmp.ncp.vm.rightsizer.service;

import com.mcmp.ncp.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;

public interface AnomalyCostVmService {

    /**
     * 일일 VM을 통해 이상비용을 탐지한다.
     *
     * @param ncpCostVmMonthDto InstanceNo, RegionCode MemberNo 담은 dto.
     * @return 이상 비용 DTO.
     */
    AnomalyDto getAnomalyCostByVmId(NcpCostVmMonthDto ncpCostVmMonthDto);

}
