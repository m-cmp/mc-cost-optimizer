package com.mcmp.azure.vm.rightsizer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

@Mapper
public interface ServiceGroupMetaMapper {

    /**
     * VM ID와 subscription ID로 servicegroup_meta 조회
     * @param vmId VM ID
     * @param subscriptionId Azure subscription ID (csp_account)
     * @return service_cd(projectCd), workspace_cd 반환
     */
    Map<String, String> selectProjectAndWorkspaceByVmId(
            @Param("vmId") String vmId,
            @Param("subscriptionId") String subscriptionId
    );
}
