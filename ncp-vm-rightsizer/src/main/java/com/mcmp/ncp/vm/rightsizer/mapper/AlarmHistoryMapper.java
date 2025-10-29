package com.mcmp.ncp.vm.rightsizer.mapper;

import com.mcmp.ncp.vm.rightsizer.dto.AlarmHistoryDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlarmHistoryMapper {

    int insertAlarmHistory(AlarmHistoryDto alarmHistoryDto);
}
