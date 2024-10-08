package com.mcmp.costselector.unused.dao;

import com.mcmp.costselector.unused.model.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    public OptiSizeTargetMetaModel getOptiSizeTargetMeta(UnusedResourceStatusModel req){
        return sqlSessionTemplate.selectOne("unused.getOptiSizeTargetMeta", req);
    }

    public OptiEC2SizeRstModel getRscEc2OptiSize(Map<String, Object> req){
        return sqlSessionTemplate.selectOne("unused.getRscEc2OptiSize", req);
    }

    public OptiEC2SizeRstModel getRscEc2ModernizeType(Map<String, Object> req){
        return sqlSessionTemplate.selectOne("unused.getRscEc2ModernizeType", req);
    }

    public void insertInstOptiRcmd(InstOptiRcmdRst req){
        sqlSessionTemplate.insert("unused.insertInstOptiRcmd", req);
    }

}
