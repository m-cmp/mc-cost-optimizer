package com.mcmp.cost.ncp.collector.mapper;

import com.mcmp.cost.ncp.collector.dto.AlarmHistoryDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlarmHistoryMapper {

    int insertAlarmHistory(AlarmHistoryDto alarmHistoryDto);
}
