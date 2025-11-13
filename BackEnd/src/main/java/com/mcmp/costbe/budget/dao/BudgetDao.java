package com.mcmp.costbe.budget.dao;

import com.mcmp.costbe.budget.model.ActualUsageItemModel;
import com.mcmp.costbe.budget.model.BudgetItemModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class BudgetDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    /** 존재하는 연도 목록 조회 */
    public List<Integer> selectDistinctYears() {
        return sqlSession.selectList("budget.selectDistinctYears");
    }

    /** 연도별 예산 조회 */
    public List<BudgetItemModel> selectBudgetByYear(Map<String, Object> params) {
        return sqlSession.selectList("budget.selectBudgetByYear", params);
    }

    /** 단일 예산 upsert */
    public void upsertBudget(BudgetItemModel item) {
        sqlSession.insert("budget.upsertBudget", item);
    }

    /** 연도별 실제 사용 금액 조회 (AWS, NCP, Azure 통합) */
    public List<ActualUsageItemModel> selectActualUsageByYear(Map<String, Object> params) {
        return sqlSession.selectList("budget.selectActualUsageByYear", params);
    }
}

