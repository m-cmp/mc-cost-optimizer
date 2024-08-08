package com.mcmp.slack_demo.common.service;

import com.mcmp.slack_demo.common.dao.CommonDao;
import com.mcmp.slack_demo.mail.model.SendMailFormModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommonService {
    @Autowired
    private CommonDao commonDao;

    public void insertAlarmHistory(SendMailFormModel model){
        commonDao.insertAlarmHistory(model);
    }

    public List<String> getAlarmMailReceivers(Map<String, String> param){
        return commonDao.getAlarmMailReceivers(param);
    }
}
