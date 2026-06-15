package com.mcmp.azure.vm.rightsizer.mapper;

import com.mcmp.azure.vm.rightsizer.dto.AnomalyDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DailyAbnormalByProductMapper {

    int insertDailyAbnormalByProduct(AnomalyDto anomalyDto);
}
