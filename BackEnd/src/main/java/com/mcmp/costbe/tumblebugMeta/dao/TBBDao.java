package com.mcmp.costbe.tumblebugMeta.dao;

import com.mcmp.costbe.tumblebugMeta.model.ResourcegroupMetaModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class TBBDao {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public void insertTBBServicegroupMeta(List<ResourcegroupMetaModel> rscMetas){
        sqlSession.insert("tbb.insertTBBServicegroupMeta", rscMetas);
    }


}
