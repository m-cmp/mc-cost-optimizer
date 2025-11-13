package com.mcmp.ncp.vm.rightsizer.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

@Mapper
public interface ServiceGroupMetaMapper {

    /**
     * member_no로 servicegroup_meta에서 project_cd, workspace_cd 조회
     * @param memberNo NCP 회원 번호
     * @return projectCd, workspaceCd를 담은 Map
     */
    Map<String, String> selectProjectAndWorkspaceByMemberNo(@Param("memberNo") String memberNo);
}
