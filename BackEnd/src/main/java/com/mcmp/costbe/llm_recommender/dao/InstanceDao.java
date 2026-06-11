package com.mcmp.costbe.llm_recommender.dao;

import com.mcmp.costbe.llm_recommender.model.ResourceInstance;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class InstanceDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public List<ResourceInstance> selectInstancesByNs(Map<String, Object> params) {
        return sqlSession.selectList("instance.selectInstancesByNs", params);
    }
}
