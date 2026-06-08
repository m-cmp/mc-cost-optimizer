package com.mcmp.costbe.llm_recommender.dao;

import com.mcmp.costbe.llm_recommender.model.RecommendationHistory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class RecommendationHistoryDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public int insertHistory(RecommendationHistory history) {
        return sqlSession.insert("recommendationHistory.insertHistory", history);
    }
}
