package com.mcmp.slack_demo.common.dao;

import com.mcmp.slack_demo.mail.model.SendMailFormModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CommonDao {
    @Autowired
    @Qualifier("sqlSessionTemplateHistory")
    private SqlSessionTemplate sqlSession;

    public void insertAlarmHistory(SendMailFormModel model){
        sqlSession.insert("HistorySql.insertAlertHistory", model);
    }

    public List<String> getAlarmMailReceivers(Map<String, String> param){
        return sqlSession.selectList("HistorySql.getAlarmMailReceivers", param);
    }

}
