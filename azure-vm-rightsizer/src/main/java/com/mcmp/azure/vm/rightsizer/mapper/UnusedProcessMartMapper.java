package com.mcmp.azure.vm.rightsizer.mapper;

import com.mcmp.azure.vm.rightsizer.dto.RecommendCandidateDto;
import com.mcmp.azure.vm.rightsizer.dto.UnusedProcessMartDto;
import com.mcmp.azure.vm.rightsizer.dto.UnusedVmDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UnusedProcessMartMapper {

    /**
     * unused_process_mart에 메트릭 평균 데이터 저장
     * (Processor에서 호출)
     *
     * @param dto 저장할 메트릭 데이터
     */
    void insertUnusedProcessMart(UnusedProcessMartDto dto);

    /**
     * 특정 VM의 14일간 메트릭 데이터 분석
     * (Processor에서 호출)
     *
     * @param resourceId VM ID
     * @return 14일간 평균, 최대값, 데이터 개수
     */
    UnusedVmDto select14DaysMetricStats(@Param("resourceId") String resourceId);

    /**
     * 4일간 메트릭 데이터 기반 SizeUp/Down 추천 대상 조회
     * (Reader에서 호출)
     *
     * @return SizeUp/Down 추천 대상 리스트
     */
    List<RecommendCandidateDto> selectRecommendCandidates();
}
