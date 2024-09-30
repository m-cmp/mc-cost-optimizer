package com.mcmp.costbe.alarm.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AlarmHistoryRstModel extends AlarmHistoryReqModel{

    private List<AlarmHistoryItemModel> alarmHistory;
}
