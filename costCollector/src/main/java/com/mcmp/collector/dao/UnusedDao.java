package com.mcmp.collector.dao;

import com.mcmp.collector.model.unused.ResourceSetModel;
import com.mcmp.collector.model.unused.ResourceSetParamModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UnusedDao {

    @Autowired
    @Qualifier("sqlSessionTemplateSimple")
    private SqlSessionTemplate sqlSessionTemplate;

    public List<ResourceSetModel> getResourceSet(ResourceSetParamModel param){
        return sqlSessionTemplate.selectList("batch.getResouces", param);
    }

}
