package com.mcmp.costbe.alarm.dao;

import com.mcmp.costbe.alarm.model.AlarmHistoryItemModel;
import com.mcmp.costbe.alarm.model.AlarmHistoryReqModel;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class AlarmDao {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public List<AlarmHistoryItemModel> getAlarmHistory(AlarmHistoryReqModel req){
        return sqlSession.selectList("alarm.getAlarmHistory", req);
    }
}
