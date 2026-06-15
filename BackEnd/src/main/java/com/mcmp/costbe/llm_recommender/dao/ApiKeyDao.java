package com.mcmp.costbe.llm_recommender.dao;

import com.mcmp.costbe.llm_recommender.model.ProviderKeyModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;

@Repository
public class ApiKeyDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public void upsertApiKey(ProviderKeyModel model) {
        sqlSession.insert("apikey.upsertApiKey", model);
    }

    public ProviderKeyModel selectApiKey(Map<String, String> params) {
        return sqlSession.selectOne("apikey.selectApiKey", params);
    }

    public int deleteApiKey(Map<String, String> params) {
        return sqlSession.delete("apikey.deleteApiKey", params);
    }
}
