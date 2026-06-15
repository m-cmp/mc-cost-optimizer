package com.mcmp.cost.ncp.collector.mapper;

import com.mcmp.cost.ncp.collector.dto.BudgetMonthlyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BudgetMonthlyMapper {

    BudgetMonthlyDto selectCurrentMonthBudget(@Param("csp") String csp);
}
