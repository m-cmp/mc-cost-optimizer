package com.mcmp.ncp.vm.rightsizer.mapper;

import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NcpRightSizeMapper {

    RecommendVmTypeDto getRecommendModernizeVmType(
            @Param("region") String region,
            @Param("instanceType") String instanceType
    );
}
