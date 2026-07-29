package com.mcmp.costbe.cur.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class CurSetupDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public void upsertCurSetup(String bucketNm) {
        sqlSession.delete("cur.deleteCurSetup");
        sqlSession.insert("cur.upsertCurSetup", bucketNm);
    }

    public void upsertUserInfo(String accountId) {
        sqlSession.insert("cur.upsertUserInfo", accountId);
    }

    public void upsertMailReceiver(String email) {
        sqlSession.insert("cur.upsertMailReceiver", email);
    }

    public String getMailReceiver() {
        return sqlSession.selectOne("cur.getMailReceiver");
    }

    public boolean isDbRegistered() {
        Integer count = sqlSession.selectOne("cur.countCurSetup");
        return count != null && count > 0;
    }
}
