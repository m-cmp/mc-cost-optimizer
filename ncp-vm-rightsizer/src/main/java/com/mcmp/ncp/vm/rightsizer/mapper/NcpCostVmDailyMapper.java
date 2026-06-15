package com.mcmp.ncp.vm.rightsizer.mapper;

import com.mcmp.ncp.vm.rightsizer.dto.NcpVmMonthlyAvgCostDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NcpCostVmDailyMapper {

    /**
     * 특정 인스턴스의 지난달 평균 비용 조회 (지난달 데이터가 없으면 전체 평균)
     */
    NcpVmMonthlyAvgCostDto getAvgCostByInstanceNoAndRegion(@Param("instanceNo") String instanceNo,
                                             @Param("region") String region);

    /**
     * 특정 region의 모든 인스턴스 평균 비용 조회 (지난달 데이터가 없으면 전체 평균)
     */
    List<NcpVmMonthlyAvgCostDto> getAvgCostByRegion(@Param("region") String region);
}
