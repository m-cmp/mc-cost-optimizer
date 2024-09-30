package com.mcmp.costbe.alarm.service;

import com.mcmp.costbe.alarm.dao.AlarmDao;
import com.mcmp.costbe.alarm.model.AlarmHistoryReqModel;
import com.mcmp.costbe.alarm.model.AlarmHistoryRstModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class AlarmHistoryService {

    @Autowired
    private AlarmDao alarmDao;

    public AlarmHistoryRstModel getAlarmHistory(AlarmHistoryReqModel req) {
        try{
            AlarmHistoryRstModel result = new AlarmHistoryRstModel();
            req.setCurDate(LocalDate.now());

            result.setCurDate(req.getCurDate());
            result.setAlarmHistory(alarmDao.getAlarmHistory(req));
            result.setSelectedCsps(req.getSelectedCsps());
            result.setSelectedProjects(req.getSelectedProjects());

            return result;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }

    }
}
