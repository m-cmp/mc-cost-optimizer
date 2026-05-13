package com.mcmp.ncp.vm.rightsizer.service.impl;

import com.mcmp.ncp.vm.rightsizer.client.TumblebugClient;
import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendCandidateDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.ncp.vm.rightsizer.mapper.NcpCostVmMonthMapper;
import com.mcmp.ncp.vm.rightsizer.mapper.ServiceGroupMetaMapper;
import com.mcmp.ncp.vm.rightsizer.service.RecommendVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendVmServiceImpl implements RecommendVmService {

    private final NcpCostVmMonthMapper ncpCostVmMonthMapper;
    private final ServiceGroupMetaMapper serviceGroupMetaMapper;
    private final TumblebugClient tumblebugClient;

    public void test() {
        ncpCostVmMonthMapper.findLatestByMemberNo("3059708").forEach(dto -> {
            this.getRecommendSizeUpVm(dto.getMemberNo(), dto.getInstanceNo());
            this.getRecommendSizeDownVm(dto.getMemberNo(), dto.getInstanceNo());
        });
    }

    @Override
    public RecommendVmTypeDto getRecommendSizeUpVm(String memberNo, String instanceNo) {
        return recommend(memberNo, instanceNo, "Up");
    }

    @Override
    public RecommendVmTypeDto getRecommendSizeDownVm(String memberNo, String instanceNo) {
        return recommend(memberNo, instanceNo, "Down");
    }

    private RecommendVmTypeDto recommend(String memberNo, String instanceNo, String direction) {
        NcpCostVmMonthDto vmInfo = ncpCostVmMonthMapper
                .findLatestByMemberNoAndInstanceNo(memberNo, instanceNo);
        if (vmInfo == null) {
            log.warn("VM not found: memberNo={}, instanceNo={}", memberNo, instanceNo);
            return null;
        }

        Map<String, String> tbbIds = serviceGroupMetaMapper.selectTbbIdentifiersByInstanceNo(instanceNo);
        if (tbbIds == null) {
            log.warn("Tumblebug 식별자 없음: instanceNo={}", instanceNo);
            return null;
        }

        RecommendCandidateDto candidate = RecommendCandidateDto.builder()
                .resourceId(instanceNo)
                .memberNo(memberNo)
                .regionCode(vmInfo.getRegionCode())
                .serverSpecCode(vmInfo.getServerSpecCode())
                .instanceName(vmInfo.getInstanceName())
                .recommendType(direction)
                .tbbNsId(tbbIds.get("tbbNsId"))
                .tbbMciId(tbbIds.get("tbbMciId"))
                .tbbVmId(tbbIds.get("tbbVmId"))
                .build();

        tumblebugClient.fillCurrentSpec(candidate);

        String recommendSpecName = tumblebugClient.recommendSpec(candidate);
        if (recommendSpecName == null) {
            log.warn("Tumblebug 추천 없음: instanceNo={}, direction={}", instanceNo, direction);
            return null;
        }

        RecommendVmTypeDto result = RecommendVmTypeDto.builder()
                .currentType(candidate.getCurrentSpecName() != null
                        ? candidate.getCurrentSpecName()
                        : vmInfo.getServerSpecCode())
                .recommendType(recommendSpecName)
                .memberNo(memberNo)
                .vmId(instanceNo)
                .plan(direction)
                .build();

        log.info("[{}] instanceNo={}, {} → {}", direction, instanceNo,
                result.getCurrentType(), result.getRecommendType());
        return result;
    }
}
