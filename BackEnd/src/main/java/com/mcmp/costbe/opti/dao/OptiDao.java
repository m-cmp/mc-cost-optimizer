package com.mcmp.costbe.opti.dao;

import com.mcmp.costbe.opti.model.UnusedQueryParamModel;
import com.mcmp.costbe.opti.model.UnusedQueryRstModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class OptiDao {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public List<UnusedQueryRstModel> getOptiUnused(UnusedQueryParamModel param){
        return sqlSession.selectList("opti.getOptiUnused", param);
    }

}
