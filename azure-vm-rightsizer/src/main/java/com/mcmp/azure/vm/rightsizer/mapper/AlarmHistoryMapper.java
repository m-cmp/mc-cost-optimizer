package com.mcmp.azure.vm.rightsizer.mapper;

import com.mcmp.azure.vm.rightsizer.dto.AlarmHistoryDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlarmHistoryMapper {

    int insertAlarmHistory(AlarmHistoryDto alarmHistoryDto);
}
