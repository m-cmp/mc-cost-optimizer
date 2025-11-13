package com.mcmp.ncp.vm.rightsizer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UnusedBatchRstMapper {

    /**
     * unused_batch_rst에 Unused 판정 결과 저장
     *
     * @param cspType CSP 타입 (AZURE, NCP)
     * @param cspAccount CSP 계정 ID
     * @param cspInstanceId 인스턴스 ID
     * @param planType Plan 타입 (Unused)
     */
    void insertUnusedBatchRst(
        @Param("cspType") String cspType,
        @Param("cspAccount") String cspAccount,
        @Param("cspInstanceId") String cspInstanceId,
        @Param("planType") String planType
    );

    /**
     * 오늘 날짜의 Unused 판정 여부 확인
     *
     * @param cspType CSP 타입
     * @param cspInstanceId 인스턴스 ID
     * @return Unused 판정 건수 (0 또는 1)
     */
    int checkTodayUnusedExists(
        @Param("cspType") String cspType,
        @Param("cspInstanceId") String cspInstanceId
    );
}
