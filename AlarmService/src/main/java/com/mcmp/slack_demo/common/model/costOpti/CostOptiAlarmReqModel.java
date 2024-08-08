package com.mcmp.slack_demo.common.model.costOpti;

import lombok.Data;

import java.util.List;

@Data
public class CostOptiAlarmReqModel extends CostOptiAlarmModel{
    private List<String> alarm_type;


}
