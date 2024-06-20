package com.mcmp.slack_demo.mail.dao;

import com.mcmp.slack_demo.mail.model.MailingInfoModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class MailingDao {
    @Autowired
    @Qualifier("sqlSessionTemplateMailing")
    private SqlSessionTemplate sqlSession;

    public void insertMailingInfo(MailingInfoModel model){
        sqlSession.insert("MailingSql.insertMailingInfo", model);
    }

    public MailingInfoModel getMailingInfo(){
        return sqlSession.selectOne("MailingSql.selectMailingInfo");
    }
}
