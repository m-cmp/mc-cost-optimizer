package com.mcmp.slack_demo.common.dao;

import com.mcmp.slack_demo.common.model.costOpti.CostOptiAlarmReqModel;
import com.mcmp.slack_demo.mail.model.SendMailFormModel;
import com.mcmp.slack_demo.slack.model.SendSlackFormModel;
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

    public int getAlertDuplicate(SendMailFormModel model){
        return sqlSession.selectOne("HistorySql.getAlertDuplicate", model);
    }

    public void insertAlarmHistory(SendMailFormModel model){
        sqlSession.insert("HistorySql.insertAlertHistory", model);
    }

    public List<String> getAlarmMailReceivers(CostOptiAlarmReqModel param){
        return sqlSession.selectList("HistorySql.getAlarmMailReceivers", param);
    }

    public int getSlackDuplicate(SendSlackFormModel model){
        return sqlSession.selectOne("HistorySql.getAlertDuplicate", model);
    }

    public void insertSlackHistory(SendSlackFormModel model){
        sqlSession.insert("HistorySql.insertAlertHistory", model);
    }
}
