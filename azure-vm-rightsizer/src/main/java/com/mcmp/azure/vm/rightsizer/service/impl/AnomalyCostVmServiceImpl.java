package com.mcmp.azure.vm.rightsizer.service.impl;

import com.mcmp.azure.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.azure.vm.rightsizer.dto.AzureCostVmDailyDto;
import com.mcmp.azure.vm.rightsizer.dto.VmMonthlyAvgCostDto;
import com.mcmp.azure.vm.rightsizer.mapper.AzureCostVmDailyMapper;
import com.mcmp.azure.vm.rightsizer.mapper.VmCostAnalysisMapper;
import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import com.mcmp.azure.vm.rightsizer.service.AnomalyCostVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnomalyCostVmServiceImpl implements AnomalyCostVmService {

    private final AzureCredentialProperties azureCredentialProperties;
    private final AzureCostVmDailyMapper azureCostVmDailyMapper;
    private final VmCostAnalysisMapper vmCostAnalysisMapper;

    public void test() {
        // Test (이상비용 로직 테스트용)
        List<AzureCostVmDailyDto> azureCostVmDailyDtoList = azureCostVmDailyMapper.findLatestBySubscriptionId(azureCredentialProperties.getSubscriptionId());

        log.info("Cost VM List Test!!!!");
        for (AzureCostVmDailyDto azureCostVmDailyDto : azureCostVmDailyDtoList) {
            this.getAnomalyCostByVmId(azureCostVmDailyDto);
        }
    }

    @Override
    public AnomalyDto getAnomalyCostByVmId(AzureCostVmDailyDto azureCostVmDailyDto) {
        VmMonthlyAvgCostDto vmMonthlyAvgCostDto = vmCostAnalysisMapper.selectMonthlyAvgCostByVmId(azureCostVmDailyDto.getVmId());
        LocalDateTime collectDate = LocalDateTime.now();
        double percentagePoint = getPercentagePoint(vmMonthlyAvgCostDto);
        AnomalyDto anomalyDto = AnomalyDto.builder()
                .collectDt(collectDate)
                .vmId(azureCostVmDailyDto.getVmId())
                .productCd("Virtual Machine(" + vmMonthlyAvgCostDto.getVmId() + ")")
                .abnormalRating(getAnomalyRating(percentagePoint))
                .percentagePoint(percentagePoint)
                .standardCost(vmMonthlyAvgCostDto.getLatestCost())
                .subjectCost(vmMonthlyAvgCostDto.getAvgCost())
                .projectCd("projectCd")
                .workspaceCd("workspaceCd")
                .cspType("AZURE")
                .build();

        log.info("[AnomalyCost] get VmId={}, AnomalyDto={}", vmMonthlyAvgCostDto.getVmId(), anomalyDto);
        return anomalyDto;
    }

    private double getPercentagePoint(VmMonthlyAvgCostDto vmMonthlyAvgCostDto) {
        if (vmMonthlyAvgCostDto.getAvgCost() == null ||
                vmMonthlyAvgCostDto.getAvgCost() == 0 ||
                vmMonthlyAvgCostDto.getLatestCost() == null) {
            return 0.0;
        }
        return ((vmMonthlyAvgCostDto.getLatestCost() - vmMonthlyAvgCostDto.getAvgCost()) / vmMonthlyAvgCostDto.getAvgCost()) * 100;
    }

    private String getAnomalyRating(double percentagePoint) {
        if (percentagePoint >= 30) {
            return "Critical";
        } else if (percentagePoint >= 20) {
            return "Caution";
        } else if (percentagePoint >= 10) {
            return "Warning";
        } else {
            return null;
        }
    }
}
