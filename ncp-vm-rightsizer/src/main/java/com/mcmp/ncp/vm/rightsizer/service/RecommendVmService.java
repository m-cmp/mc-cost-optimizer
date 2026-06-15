package com.mcmp.ncp.vm.rightsizer.service;

import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;

public interface RecommendVmService {

    RecommendVmTypeDto getRecommendSizeUpVm(String memberNo, String instanceNo);

    RecommendVmTypeDto getRecommendSizeDownVm(String memberNo, String instanceNo);

}
