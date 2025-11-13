package com.mcmp.collector.dao;

import com.mcmp.collector.dto.BudgetUsageDto;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AWS 예산 체크용 DAO
 */
@Repository
public class BudgetCheckDao {

    @Autowired
    @Qualifier("sqlSessionTemplateSimple")
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * 예산 80% 이상 사용한 프로젝트 목록 조회 (AWS)
     * @param yearMonth 조회할 년월 (YYYYMM 형식)
     * @return 예산 초과 프로젝트 목록
     */
    public List<BudgetUsageDto> selectBudgetExceededProjects(String yearMonth) {
        return sqlSessionTemplate.selectList("budgetCheck.selectBudgetExceededProjects", yearMonth);
    }
}
