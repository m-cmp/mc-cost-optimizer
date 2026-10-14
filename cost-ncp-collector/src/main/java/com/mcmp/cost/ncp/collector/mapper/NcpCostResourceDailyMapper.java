package com.mcmp.cost.ncp.collector.mapper;

import com.mcmp.cost.ncp.collector.entity.NcpCostResourceMonth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface NcpCostResourceDailyMapper {

    /**
     * 자원 월 누적 행 INSERT (UNIQUE 충돌 시 무시).
     * @return INSERT 된 행 수 (이미 적재된 스냅샷이면 0)
     */
    int insertResourceMonthIgnore(@Param("row") NcpCostResourceMonth row);

    /**
     * 월 누적 → 일별 차분 INSERT (UNIQUE 충돌 시 무시). ncp_cost_vm_daily 의 insertDailyCost 와 같은 방식,
     * 조인 키만 (member_no, demand_month, resource_type, resource_id, region_code, demand_type_detail_code, contract_no) 로 확장.
     */
    int insertDailyCost(@Param("row") NcpCostResourceMonth row, @Param("targetDate") LocalDate targetDate);

    /**
     * NKS 목록에 없는(삭제된) 클러스터의 UUID 를 과거 적재 행에서 복원. 없으면 null.
     */
    String selectResolvedResourceId(@Param("memberNo") String memberNo, @Param("instanceNo") String instanceNo);
}
