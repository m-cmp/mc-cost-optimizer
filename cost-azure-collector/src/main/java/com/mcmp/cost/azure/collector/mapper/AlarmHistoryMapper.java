package com.mcmp.cost.azure.collector.mapper;

import com.mcmp.cost.azure.collector.dto.AlarmHistoryDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlarmHistoryMapper {

    int insertAlarmHistory(AlarmHistoryDto alarmHistoryDto);
}
