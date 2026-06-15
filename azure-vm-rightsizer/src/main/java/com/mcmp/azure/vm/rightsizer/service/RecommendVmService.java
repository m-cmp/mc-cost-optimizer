package com.mcmp.azure.vm.rightsizer.service;

import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;

public interface RecommendVmService {

    /**
     * SizeUp VM 추천 타입을 조회한다.
     *
     * @param vmId VM 아이디.
     * @return 추천 VM 타입.
     */
    RecommendVmTypeDto getRecommendSizeUpVm(String vmId);

    /**
     * SizeDown VM 추천 타입을 조회한다.
     *
     * @param vmId VM 아이디.
     * @return 추천 VM 타입.
     */
    RecommendVmTypeDto getRecommendSizeDownVm(String vmId);

    /**
     * modernize VM 추천 타입을 조회한다.
     *
     * @param vmId VM 아이디.
     * @return 추천 VM 타입.
     */
    RecommendVmTypeDto getRecommendModernizeVm(String vmId);
}
