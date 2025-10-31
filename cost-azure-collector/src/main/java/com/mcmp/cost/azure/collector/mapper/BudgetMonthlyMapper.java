package com.mcmp.cost.azure.collector.mapper;

import com.mcmp.cost.azure.collector.dto.BudgetMonthlyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BudgetMonthlyMapper {

    BudgetMonthlyDto selectCurrentMonthBudget(@Param("csp") String csp);
}
