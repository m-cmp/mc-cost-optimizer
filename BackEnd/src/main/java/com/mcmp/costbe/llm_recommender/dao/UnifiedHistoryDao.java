package com.mcmp.costbe.llm_recommender.dao;

import com.mcmp.costbe.llm_recommender.model.UnifiedHistoryRow;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class UnifiedHistoryDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public List<UnifiedHistoryRow> selectAlarmRecommendations(Map<String, Object> params) {
        return sqlSession.selectList("unifiedHistory.selectAlarmRecommendations", params);
    }

    public List<UnifiedHistoryRow> selectLlmRecommendations(Map<String, Object> params) {
        return sqlSession.selectList("unifiedHistory.selectLlmRecommendations", params);
    }
}
