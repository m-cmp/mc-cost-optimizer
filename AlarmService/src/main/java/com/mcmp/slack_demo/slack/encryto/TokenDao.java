package com.mcmp.slack_demo.slack.encryto;


import com.mcmp.slack_demo.slack.model.SaveTokenModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class TokenDao {

    @Autowired
    private SqlSessionTemplate sqlSession;
//    private SqlSessionTemplate sqlSession;
//    @Autowired
//    public TokenDao(@Qualifier("sqlSessionTemplateSlack") SqlSessionTemplate sqlSession) {
//        this.sqlSession = sqlSession;
//    }
    public void saveEncryptedToken(SaveTokenModel model){
        sqlSession.insert("TokenSql.saveEncryptedToken", model);
    }

    public String getEncryptedToken(String id){
        return sqlSession.selectOne("TokenSql.getEncryptedToken", id);
    }

    public String getEncryptedChannel(String id){
        return sqlSession.selectOne("TokenSql.getEncryptedChannel", id);
    }
}
