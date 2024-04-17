package com.mcmp.dummybe.model.dashboard;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Data
public class AbnormalUserWidget extends UserWidget {
    private Integer threshold;
    private String colId;
    private String sortType;
    private String columnState;
    private double minAlert;
    private double maxAlert;
    private Object mailSendCond;
    private Object mailReceivers;
    private String alarmCondition;
    private Boolean isIncludeLowerLevel;

    public AbnormalUserWidget(){
        this.threshold = 10;
        this.colId = "increaseDecreaseRate";
        this.sortType = "desc";
        this.columnState = "";
        this.minAlert = 100.0;
        this.maxAlert = 1000.0;
        this.mailSendCond = makeMailSendCondObject();
        this.mailReceivers = makeMailReceiversObject();
        this.alarmCondition = "I";
        this.isIncludeLowerLevel = false;
    }

    private Map<String, Object> makeMailSendCondObject(){
        Map<String, Object> mailSendCond = new HashMap<>();
        mailSendCond.put("alarmCond","I");
        mailSendCond.put("isIncludeLowerLevel",false);
        mailSendCond.put("mailReceivers",makeMailReceiversObject());

        return mailSendCond;
    }
    private Map<String, Object> makeMailReceiversObject(){
        Map<String, Object> mailReceivers = new HashMap<>();
        mailReceivers.put("normal",new ArrayList<>());
        mailReceivers.put("minor",new ArrayList<>());
        mailReceivers.put("critical",new ArrayList<>());

        return mailReceivers;
    }

}
