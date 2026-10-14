package com.mcmp.costbe.gcp.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;

@Repository
public class GcpSetupDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public void upsertGcpSetup(String datasetNm, String tableNm) {
        sqlSession.delete("gcp.deleteGcpSetup");
        sqlSession.insert("gcp.upsertGcpSetup", Map.of("datasetNm", datasetNm, "tableNm", tableNm != null ? tableNm : ""));
    }

    public boolean isDbRegistered() {
        Integer count = sqlSession.selectOne("gcp.countGcpSetup");
        return count != null && count > 0;
    }
}
