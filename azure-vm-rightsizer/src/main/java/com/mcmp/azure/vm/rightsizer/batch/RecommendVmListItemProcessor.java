package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.dto.RecommendCandidateDto;
import com.mcmp.azure.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.azure.vm.rightsizer.mapper.AzureRightSizeMapper;
import com.mcmp.azure.vm.rightsizer.mapper.UnusedBatchRstMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class RecommendVmListItemProcessor implements ItemProcessor<RecommendCandidateDto, RecommendVmTypeDto> {

    private final AzureRightSizeMapper azureRightSizeMapper;
    private final UnusedBatchRstMapper unusedBatchRstMapper;

    @Override
    public RecommendVmTypeDto process(RecommendCandidateDto candidate) throws Exception {
        log.info("Processing Recommend: resourceId={}, type={}, avgCpu={}, maxCpu={}",
            candidate.getResourceId(),
            candidate.getRecommendType(),
            candidate.getAvg4DaysCpu(),
            candidate.getMax4DaysCpu());

        // Unused Job에서 이미 처리된 VM인지 확인 (중복 알림 방지)
        int unusedCount = unusedBatchRstMapper.checkTodayUnusedExists("AZURE", candidate.getVmId());
        if (unusedCount > 0) {
            log.info("Skipping Recommend: VM already processed by Unused Job today. vmId={}", candidate.getVmId());
            return null;
        }

        RecommendVmTypeDto result;

        if ("Up".equals(candidate.getRecommendType())) {
            // SizeUp: Mapper 직접 호출
            result = azureRightSizeMapper.getRecommendSizeUpVmType(
                candidate.getOsType(),
                candidate.getRegion(),
                candidate.getInstanceType()
            );
        } else if ("Down".equals(candidate.getRecommendType())) {
            // SizeDown: Mapper 직접 호출
            result = azureRightSizeMapper.getRecommendSizeDownVmType(
                candidate.getOsType(),
                candidate.getRegion(),
                candidate.getInstanceType(),
                0.5  // discountRate
            );
        } else if ("Modernize".equals(candidate.getRecommendType())) {
            // Modernize: 매핑 테이블 기반 신규 세대로 업그레이드
            result = azureRightSizeMapper.getRecommendModernizeVmType(
                candidate.getOsType(),
                candidate.getRegion(),
                candidate.getInstanceType()
            );
        } else {
            log.warn("Unknown recommend type: {}", candidate.getRecommendType());
            return null;
        }

        if (result == null) {
            log.warn("No recommendation found for resourceId={}, currentType={}",
                candidate.getResourceId(),
                candidate.getInstanceType());
            return null;
        }

        // Modernize: 추천 VM이 기존 VM과 동일하면 알림 보내지 않음
        if ("Modernize".equals(candidate.getRecommendType())) {
            if (result.getRecommendType() != null &&
                result.getRecommendType().equals(candidate.getInstanceType())) {
                log.info("Modernize skipped: Recommended VM type is same as current type. vmId={}, currentType={}",
                    candidate.getVmId(),
                    candidate.getInstanceType());
                return null;
            }
        }

        // DTO 조립
        result.setVmId(candidate.getVmId());
        result.setCurrentType(candidate.getInstanceType());
        result.setPlan(candidate.getRecommendType());  // "Up" or "Down"

        log.info("Recommendation generated: vmId={}, currentType={}, recommendType={}, plan={}",
            result.getVmId(),
            result.getCurrentType(),
            result.getRecommendType(),
            result.getPlan());

        return result;
    }
}
