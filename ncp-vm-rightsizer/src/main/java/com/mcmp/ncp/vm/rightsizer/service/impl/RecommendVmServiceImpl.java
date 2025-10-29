package com.mcmp.ncp.vm.rightsizer.service.impl;

import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.ncp.vm.rightsizer.mapper.NcpCostVmMonthMapper;
import com.mcmp.ncp.vm.rightsizer.mapper.NcpRightSizeMapper;
import com.mcmp.ncp.vm.rightsizer.service.RecommendVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendVmServiceImpl implements RecommendVmService {

    private final NcpRightSizeMapper ncpRightSizeMapper;
    private final NcpCostVmMonthMapper ncpCostVmMonthMapper;

    public void test() {
        // Test (전체 추천 로직 테스트용)
        List<NcpCostVmMonthDto> ncpCostVmMonthDtoList = ncpCostVmMonthMapper.findLatestByMemberNo("3059708");

        log.info("VM List Test!!!!");
        for (NcpCostVmMonthDto dto : ncpCostVmMonthDtoList) {
            this.getRecommendSizeUpVm(dto.getMemberNo(), dto.getInstanceNo());
            this.getRecommendSizeDownVm(dto.getMemberNo(), dto.getInstanceNo());
        }
    }

    @Override
    public RecommendVmTypeDto getRecommendSizeUpVm(String memberNo, String instanceNo) {
        NcpCostVmMonthDto dto = ncpCostVmMonthMapper
                .findLatestByMemberNoAndInstanceNo(memberNo, instanceNo);
        RecommendVmTypeDto sizeUpDto = ncpRightSizeMapper.getRecommendSizeUpVmType(dto.getRegionCode(), dto.getServerSpecCode());
        if (sizeUpDto != null) {
            sizeUpDto.setMemberNo(dto.getMemberNo());
            sizeUpDto.setVmId(dto.getInstanceNo());
            sizeUpDto.setCurrentType(dto.getServerSpecCode());
            log.info("[size up] vmId={}, vmDto={}", sizeUpDto.getVmId(), sizeUpDto);
            return sizeUpDto;
        }
        return null;
    }

    @Override
    public RecommendVmTypeDto getRecommendSizeDownVm(String memberNo, String instanceNo) {
        NcpCostVmMonthDto dto = ncpCostVmMonthMapper
                .findLatestByMemberNoAndInstanceNo(memberNo, instanceNo);
        RecommendVmTypeDto sizeDownDto = ncpRightSizeMapper.getRecommendSizeDownVmType(dto.getRegionCode(), dto.getServerSpecCode(), 0.5);
        if (sizeDownDto != null) {
            sizeDownDto.setMemberNo(dto.getMemberNo());
            sizeDownDto.setVmId(dto.getInstanceNo());
            sizeDownDto.setCurrentType(dto.getServerSpecCode());
            log.info("[size down] vmId={}, vmDto={}", sizeDownDto.getVmId(), sizeDownDto);
            return sizeDownDto;
        }
        return null;
    }
}
