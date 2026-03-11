package com.mcmp.azure.vm.rightsizer.batch;

import com.mcmp.azure.vm.rightsizer.client.TumblebugClient;
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
    private final TumblebugClient tumblebugClient;

    @Override
    public RecommendVmTypeDto process(RecommendCandidateDto candidate) throws Exception {
        log.info("Processing Recommend: resourceId={}, type={}, avgCpu={}, maxCpu={}",
            candidate.getResourceId(),
            candidate.getRecommendType(),
            candidate.getAvg4DaysCpu(),
            candidate.getMax4DaysCpu());

        // Unused Job에서 이미 처리된 VM인지 확인 (중복 알림 방지)
        int unusedCount = unusedBatchRstMapper.checkTodayUnusedExists("AZURE", candidate.getResourceId());
        if (unusedCount > 0) {
            log.info("Skipping Recommend: VM already processed by Unused Job today. resourceId={}", candidate.getResourceId());
            return null;
        }

        RecommendVmTypeDto result;

        if ("Up".equals(candidate.getRecommendType()) || "Down".equals(candidate.getRecommendType())) {
            // Up/Down: Tumblebug recommendSpec API 사용
            tumblebugClient.fillCurrentSpec(candidate);

            String recommendSpecName = tumblebugClient.recommendSpec(candidate);
            if (recommendSpecName == null) {
                log.warn("No recommendation from Tumblebug for resourceId={}, direction={}",
                    candidate.getResourceId(), candidate.getRecommendType());
                return null;
            }

            // 추천 스펙이 현재와 동일하면 스킵
            if (recommendSpecName.equals(candidate.getCurrentSpecName())) {
                log.info("Skipping: recommended spec is same as current. resourceId={}, spec={}",
                    candidate.getResourceId(), recommendSpecName);
                return null;
            }

            result = RecommendVmTypeDto.builder()
                .currentType(candidate.getCurrentSpecName() != null
                    ? candidate.getCurrentSpecName()
                    : candidate.getInstanceType())
                .recommendType(recommendSpecName)
                .build();

        } else if ("Modernize".equals(candidate.getRecommendType())) {
            // Modernize: 기존 DB 방식 유지
            result = azureRightSizeMapper.getRecommendModernizeVmType(
                candidate.getOsType(),
                candidate.getRegion(),
                candidate.getInstanceType()
            );

            if (result == null) {
                log.warn("No recommendation found for resourceId={}, currentType={}",
                    candidate.getResourceId(), candidate.getInstanceType());
                return null;
            }

            if (result.getRecommendType() != null &&
                result.getRecommendType().equals(candidate.getInstanceType())) {
                log.info("Modernize skipped: Recommended VM type is same as current type. vmId={}, currentType={}",
                    candidate.getVmId(), candidate.getInstanceType());
                return null;
            }

        } else {
            log.warn("Unknown recommend type: {}", candidate.getRecommendType());
            return null;
        }

        // DTO 조립
        result.setVmId(candidate.getVmId());
        if (result.getCurrentType() == null) {
            result.setCurrentType(candidate.getInstanceType());
        }
        result.setPlan(candidate.getRecommendType());

        log.info("Recommendation generated: vmId={}, currentType={}, recommendType={}, plan={}",
            result.getVmId(),
            result.getCurrentType(),
            result.getRecommendType(),
            result.getPlan());

        return result;
    }
}
