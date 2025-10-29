package com.mcmp.azure.vm.rightsizer.mapper;

import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AzureCostVmDailyMapper {

    List<AzureCostVmDailyDto> findLatestBySubscriptionId(@Param("subscriptionId") String subscriptionId);

    AzureCostVmDailyDto findLatestBySubscriptionIdAndVmId(
            @Param("subscriptionId") String subscriptionId,
            @Param("vmId") String vmId
    );
}
