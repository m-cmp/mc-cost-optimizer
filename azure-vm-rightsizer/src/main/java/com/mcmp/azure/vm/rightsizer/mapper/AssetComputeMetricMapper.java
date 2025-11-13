package com.mcmp.azure.vm.rightsizer.mapper;

import com.mcmp.azure.vm.rightsizer.dto.UnusedVmDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AssetComputeMetricMapper {

    /**
     * 어제 날짜의 VM별 평균 CPU 사용률 조회
     * (ItemReader에서 사용)
     *
     * @param cspType CSP 타입 (AZURE, NCP)
     * @return 어제 날짜의 VM별 평균 CPU 사용률 리스트
     */
    List<UnusedVmDto> selectYesterdayAvgCpuByVm(@Param("cspType") String cspType);
}
