package com.mcmp.ncp.vm.rightsizer.mapper;

import com.mcmp.ncp.vm.rightsizer.dto.AnomalyDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DailyAbnormalByProductMapper {

    int insertDailyAbnormalByProduct(AnomalyDto anomalyDto);
}
