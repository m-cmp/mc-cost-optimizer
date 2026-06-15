package com.mcmp.azure.vm.rightsizer.mapper;

import com.mcmp.azure.vm.rightsizer.dto.VmMonthlyAvgCostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface VmCostAnalysisMapper {

    /**
     * 특정 VM의 월별 평균 비용 조회
     * (지난달 데이터가 있으면 지난달, 없으면 전체 평균)
     */
    VmMonthlyAvgCostDto selectMonthlyAvgCostByVmId(@Param("vmId") String vmId);

    /**
     * 전체 VM의 월별 평균 비용 조회
     */
    List<VmMonthlyAvgCostDto> selectMonthlyAvgCostAllVm();
}
