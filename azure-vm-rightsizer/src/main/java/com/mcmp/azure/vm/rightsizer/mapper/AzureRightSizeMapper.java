package com.mcmp.azure.vm.rightsizer.mapper;

import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AzureRightSizeMapper {

    RecommendVmTypeDto getRecommendSizeUpVmType(
            @Param("osType") String osType,
            @Param("region") String region,
            @Param("instanceType") String instanceType
    );

    RecommendVmTypeDto getRecommendSizeDownVmType(
            @Param("osType") String osType,
            @Param("region") String region,
            @Param("instanceType") String instanceType,
            @Param("discountRate") double discountRate
    );

    RecommendVmTypeDto getRecommendModernizeVmType(
            @Param("osType") String osType,
            @Param("region") String region,
            @Param("instanceType") String instanceType
    );

}
