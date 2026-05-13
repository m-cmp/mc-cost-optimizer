package com.mcmp.cost.ncp.collector.mapper;

import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;

@Mapper
public interface NcpCostVmDailyMapper {

    /**
     * 특정 인스턴스의 특정 날짜 일일 비용 데이터 INSERT
     *
     * @param instanceNo 인스턴스 번호
     * @param targetDate 대상 날짜
     * @return INSERT된 행 수
     */
    int insertDailyCost(@Param("instanceNo") String instanceNo,
                        @Param("targetDate") LocalDate targetDate);

    /**
     * VM 월별 비용 데이터 INSERT (중복 무시)
     *
     * @param vmMonth VM 월별 비용 데이터
     * @return INSERT된 행 수 (중복이면 0)
     */
    int insertVmMonthIgnore(@Param("vmMonth") NcpCostVmMonth vmMonth);
}
