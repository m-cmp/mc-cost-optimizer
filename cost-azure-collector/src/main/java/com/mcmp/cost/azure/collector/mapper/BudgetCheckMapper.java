package com.mcmp.cost.azure.collector.mapper;

import com.mcmp.cost.azure.collector.dto.BudgetUsageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BudgetCheckMapper {

    /**
     * 예산 80% 이상 사용한 프로젝트 목록 조회 (Azure)
     * - azure_cost_vm_daily와 servicegroup_meta 조인
     * - 프로젝트별, CSP별 이번 달 총 비용 집계
     * - budget_monthly와 비교하여 80% 이상만 반환
     */
    List<BudgetUsageDto> selectBudgetExceededProjects();
}
