package com.mcmp.azure.vm.rightsizer.service.impl;

import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.azure.vm.rightsizer.mapper.AzureCostVmDailyMapper;
import com.mcmp.azure.vm.rightsizer.mapper.AzureRightSizeMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import com.mcmp.azure.vm.rightsizer.service.RecommendVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendVmServiceImpl implements RecommendVmService {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AzureCostVmDailyMapper azureCostVmDailyMapper;
    private final AzureRightSizeMapper azureRightSizeMapper;

    // FIXME : 현재 메트릭과 관련한 정보가 확정되면 해당 부분 인터페이스에 맞게 개발필요
    public void test() {
        // Test (전체 추천 로직 테스트용)
        List<AzureCostVmDailyDto> azureCostVmDailyDtoList = azureCostVmDailyMapper.findLatestBySubscriptionId(azureCredentialProperties.getSubscriptionId());

        log.info("VM List Test!!!!");
        for (AzureCostVmDailyDto dto : azureCostVmDailyDtoList) {
            this.getRecommendSizeUpVm(dto.getVmId());
            this.getRecommendSizeDownVm(dto.getVmId());
            this.getRecommendModernizeVm(dto.getVmId());
        }
    }

    @Override
    public RecommendVmTypeDto getRecommendSizeUpVm(String vmId) {
        AzureCostVmDailyDto dto = azureCostVmDailyMapper
                .findLatestBySubscriptionIdAndVmId(azureCredentialProperties.getSubscriptionId(), vmId);
        RecommendVmTypeDto sizeUpDto = azureRightSizeMapper.getRecommendSizeUpVmType(dto.getOsType(), dto.getRegion(), dto.getInstanceType());
        if (sizeUpDto != null) {
            sizeUpDto.setVmId(dto.getVmId());
            sizeUpDto.setCurrentType(dto.getInstanceType());
            log.info("[size up] vmId={}, vmDto={}", sizeUpDto.getVmId(), sizeUpDto);
            return sizeUpDto;
        }
        return null;
    }

    @Override
    public RecommendVmTypeDto getRecommendSizeDownVm(String vmId) {
        AzureCostVmDailyDto dto = azureCostVmDailyMapper
                .findLatestBySubscriptionIdAndVmId(azureCredentialProperties.getSubscriptionId(), vmId);
        RecommendVmTypeDto sizeDownDto = azureRightSizeMapper.getRecommendSizeDownVmType(dto.getOsType(), dto.getRegion(), dto.getInstanceType(), 0.5);
        if (sizeDownDto != null) {
            sizeDownDto.setVmId(dto.getVmId());
            sizeDownDto.setCurrentType(dto.getInstanceType());
            log.info("[size down] vmId={}, vmDto={}", sizeDownDto.getVmId(), sizeDownDto);
            return sizeDownDto;
        }
        return null;
    }

    @Override
    public RecommendVmTypeDto getRecommendModernizeVm(String vmId) {
        AzureCostVmDailyDto dto = azureCostVmDailyMapper
                .findLatestBySubscriptionIdAndVmId(azureCredentialProperties.getSubscriptionId(), vmId);
        RecommendVmTypeDto modernizeDto = azureRightSizeMapper.getRecommendModernizeVmType(dto.getOsType(), dto.getRegion(), dto.getInstanceType());
        if (modernizeDto != null) {
            modernizeDto.setVmId(dto.getVmId());
            modernizeDto.setCurrentType(dto.getInstanceType());
            log.info("[modernize] vmId={}, vmDto={}", modernizeDto.getVmId(), modernizeDto);
            return modernizeDto;
        }
        return null;
    }
}
