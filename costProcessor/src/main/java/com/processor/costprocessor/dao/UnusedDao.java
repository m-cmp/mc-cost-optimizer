package com.processor.costprocessor.dao;

import com.processor.costprocessor.model.unused.DailyAssetAmountModel;
import com.processor.costprocessor.model.unused.DailyAssetAmountParamModel;
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

    public List<DailyAssetAmountModel> getDailyAssetAmount(DailyAssetAmountParamModel params){
        return sqlSessionTemplate.selectList("aws.getDailyAssetAmount", params);
    }
}
