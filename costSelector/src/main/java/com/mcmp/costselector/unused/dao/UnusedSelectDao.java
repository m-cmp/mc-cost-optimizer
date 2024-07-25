package com.mcmp.costselector.unused.dao;

import com.mcmp.costselector.unused.model.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UnusedSelectDao {

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public UnusedResourceStatusModel getResourceStatus(UnusedSelectReqModel req){
        return sqlSessionTemplate.selectOne("unused.getResourceStatus", req);
    }

    public List<UserAssetRSOPTModel> getUserAssetRSOPT(String req){
        return sqlSessionTemplate.selectList("unused.getUserAssetRSOPT", req);
    }

    public CpuAssetMartModel getCPUAssetMart(AssetMartReqModel req){
        return sqlSessionTemplate.selectOne("unused.getCPUAssetMart", req);
    }

    public NetworkAssetMartModel getNetworkMart(AssetMartReqModel req){
        return sqlSessionTemplate.selectOne("unused.getNetworkMart", req);
    }

    public void insertBatchRst(UnusedBatchRstModel req){
        sqlSessionTemplate.insert("unused.insertBatchRst", req);
    }

}
