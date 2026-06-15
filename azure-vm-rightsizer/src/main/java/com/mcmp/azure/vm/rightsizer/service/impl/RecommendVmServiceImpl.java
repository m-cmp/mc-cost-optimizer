package com.mcmp.azure.vm.rightsizer.service.impl;

import com.mcmp.azure.vm.rightsizer.client.TumblebugClient;
import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendCandidateDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.azure.vm.rightsizer.mapper.AzureCostVmDailyMapper;
import com.mcmp.azure.vm.rightsizer.mapper.ServiceGroupMetaMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import com.mcmp.azure.vm.rightsizer.service.RecommendVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendVmServiceImpl implements RecommendVmService {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AzureCostVmDailyMapper azureCostVmDailyMapper;
    private final ServiceGroupMetaMapper serviceGroupMetaMapper;
    private final TumblebugClient tumblebugClient;

    // FIXME : 현재 메트릭과 관련한 정보가 확정되면 해당 부분 인터페이스에 맞게 개발필요
    public void test() {
        List<AzureCostVmDailyDto> azureCostVmDailyDtoList = azureCostVmDailyMapper
                .findLatestBySubscriptionId(azureCredentialProperties.getSubscriptionId());

        log.info("VM List Test!!!!");
        for (AzureCostVmDailyDto dto : azureCostVmDailyDtoList) {
            this.getRecommendSizeUpVm(dto.getVmId());
            this.getRecommendSizeDownVm(dto.getVmId());
            this.getRecommendModernizeVm(dto.getVmId());
        }
    }

    @Override
    public RecommendVmTypeDto getRecommendSizeUpVm(String vmId) {
        return recommend(vmId, "Up");
    }

    @Override
    public RecommendVmTypeDto getRecommendSizeDownVm(String vmId) {
        return recommend(vmId, "Down");
    }

    @Override
    public RecommendVmTypeDto getRecommendModernizeVm(String vmId) {
        AzureCostVmDailyDto dto = azureCostVmDailyMapper
                .findLatestBySubscriptionIdAndVmId(azureCredentialProperties.getSubscriptionId(), vmId);
        if (dto == null) {
            log.warn("VM not found: vmId={}", vmId);
            return null;
        }

        Map<String, String> tbbIds = serviceGroupMetaMapper.selectTbbIdentifiersByResourceId(dto.getResourceId());
        if (tbbIds == null) {
            log.warn("Tumblebug 식별자 없음: resourceId={}", dto.getResourceId());
            return null;
        }

        RecommendCandidateDto candidate = RecommendCandidateDto.builder()
                .resourceId(dto.getResourceId())
                .vmId(vmId)
                .instanceType(dto.getInstanceType())
                .region(dto.getRegion())
                .osType(dto.getOsType())
                .recommendType("Modernize")
                .tbbNsId(tbbIds.get("tbbNsId"))
                .tbbMciId(tbbIds.get("tbbMciId"))
                .tbbVmId(tbbIds.get("tbbVmId"))
                .build();

        String modernizeSpecName = tumblebugClient.findModernizeSpec(candidate);
        if (modernizeSpecName == null) {
            log.warn("Tumblebug Modernize 추천 없음: vmId={}", vmId);
            return null;
        }

        RecommendVmTypeDto result = RecommendVmTypeDto.builder()
                .currentType(dto.getInstanceType())
                .recommendType(modernizeSpecName)
                .vmId(vmId)
                .plan("Modernize")
                .build();

        log.info("[Modernize] vmId={}, {} → {}", vmId, dto.getInstanceType(), modernizeSpecName);
        return result;
    }

    private RecommendVmTypeDto recommend(String vmId, String direction) {
        AzureCostVmDailyDto dto = azureCostVmDailyMapper
                .findLatestBySubscriptionIdAndVmId(azureCredentialProperties.getSubscriptionId(), vmId);
        if (dto == null) {
            log.warn("VM not found: vmId={}", vmId);
            return null;
        }

        Map<String, String> tbbIds = serviceGroupMetaMapper.selectTbbIdentifiersByResourceId(dto.getResourceId());
        if (tbbIds == null) {
            log.warn("Tumblebug 식별자 없음: resourceId={}", dto.getResourceId());
            return null;
        }

        RecommendCandidateDto candidate = RecommendCandidateDto.builder()
                .resourceId(dto.getResourceId())
                .vmId(vmId)
                .instanceType(dto.getInstanceType())
                .region(dto.getRegion())
                .osType(dto.getOsType())
                .recommendType(direction)
                .tbbNsId(tbbIds.get("tbbNsId"))
                .tbbMciId(tbbIds.get("tbbMciId"))
                .tbbVmId(tbbIds.get("tbbVmId"))
                .build();

        tumblebugClient.fillCurrentSpec(candidate);

        String recommendSpecName = tumblebugClient.recommendSpec(candidate);
        if (recommendSpecName == null) {
            log.warn("Tumblebug 추천 없음: vmId={}, direction={}", vmId, direction);
            return null;
        }

        RecommendVmTypeDto result = RecommendVmTypeDto.builder()
                .currentType(candidate.getCurrentSpecName() != null
                        ? candidate.getCurrentSpecName()
                        : dto.getInstanceType())
                .recommendType(recommendSpecName)
                .vmId(vmId)
                .plan(direction)
                .build();

        log.info("[{}] vmId={}, {} → {}", direction, vmId, result.getCurrentType(), result.getRecommendType());
        return result;
    }
}
