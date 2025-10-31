package com.mcmp.ncp.vm.rightsizer.service.impl;

import com.mcmp.ncp.vm.rightsizer.dto.AnomalyDto;
import com.mcmp.ncp.vm.rightsizer.dto.NcpCostVmMonthDto;
import com.mcmp.ncp.vm.rightsizer.dto.NcpVmMonthlyAvgCostDto;
import com.mcmp.ncp.vm.rightsizer.mapper.NcpCostVmDailyMapper;
import com.mcmp.ncp.vm.rightsizer.mapper.NcpCostVmMonthMapper;
import com.mcmp.ncp.vm.rightsizer.service.AnomalyCostVmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnomalyCostVmServiceImpl implements AnomalyCostVmService {

    private final NcpCostVmDailyMapper ncpCostVmDailyMapper;
    private final NcpCostVmMonthMapper ncpCostVmMonthMapper;

    public void test() {
        // Test (전체 추천 로직 테스트용)
        List<NcpCostVmMonthDto> ncpCostVmMonthDtoList = ncpCostVmMonthMapper.findVmListCurrentMonth();

        log.info("Anomaly VM List Test!!!!");
        for (NcpCostVmMonthDto ncpCostVmMonthDto : ncpCostVmMonthDtoList) {
            this.getAnomalyCostByVmId(ncpCostVmMonthDto);
        }
    }

    @Override
    public AnomalyDto getAnomalyCostByVmId(NcpCostVmMonthDto ncpCostVmMonthDto) {
        // 지난달 평균 금액.
        NcpVmMonthlyAvgCostDto ncpVmMonthlyAvgCostDto = ncpCostVmDailyMapper.getAvgCostByInstanceNoAndRegion(ncpCostVmMonthDto.getInstanceNo(), ncpCostVmMonthDto.getRegionCode());

        // 오늘 평균 금액
        LocalDateTime collectDate = LocalDateTime.now();
        double percentagePoint = getPercentagePoint(ncpVmMonthlyAvgCostDto);
        AnomalyDto anomalyDto = AnomalyDto.builder()
                .collectDt(collectDate)
                .vmId(ncpVmMonthlyAvgCostDto.getInstanceNo())
                .memberNo(ncpCostVmMonthDto.getMemberNo())
                .productCd("Virtual Machine(" + ncpVmMonthlyAvgCostDto.getInstanceNo() + ")")
                .abnormalRating(getAnomalyRating(percentagePoint))
                .percentagePoint(percentagePoint)
                .standardCost(ncpVmMonthlyAvgCostDto.getLatestCost())
                .subjectCost(ncpVmMonthlyAvgCostDto.getAvgCost())
                .projectCd("projectCd")
                .workspaceCd("workspaceCd")
                .cspType("NCP")
                .build();

        log.info("[AnomalyCost] get VmId={}, AnomalyDto={}", ncpVmMonthlyAvgCostDto.getInstanceNo(), anomalyDto);
        return anomalyDto;
    }

    private double getPercentagePoint(NcpVmMonthlyAvgCostDto ncpVmMonthlyAvgCostDto) {
        if (ncpVmMonthlyAvgCostDto.getAvgCost() == null ||
                ncpVmMonthlyAvgCostDto.getAvgCost() == 0 ||
                ncpVmMonthlyAvgCostDto.getLatestCost() == null) {
            return 0.0;
        }
        return ((ncpVmMonthlyAvgCostDto.getLatestCost() - ncpVmMonthlyAvgCostDto.getAvgCost()) / ncpVmMonthlyAvgCostDto.getAvgCost()) * 100;
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
