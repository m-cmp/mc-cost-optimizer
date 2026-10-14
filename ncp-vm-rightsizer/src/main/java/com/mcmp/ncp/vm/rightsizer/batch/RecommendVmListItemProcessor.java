package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.client.TumblebugClient;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendCandidateDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.ncp.vm.rightsizer.mapper.UnusedBatchRstMapper;
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

    private final UnusedBatchRstMapper unusedBatchRstMapper;
    private final TumblebugClient tumblebugClient;

    @Override
    public RecommendVmTypeDto process(RecommendCandidateDto candidate) throws Exception {
        log.info("Processing Recommend: resourceId={}, type={}, avgCpu={}, maxCpu={}",
            candidate.getResourceId(),
            candidate.getRecommendType(),
            candidate.getAvg4DaysCpu(),
            candidate.getMax4DaysCpu());

        // Unused Job에서 이미 처리된 Instance인지 확인 (중복 알림 방지)
        int unusedCount = unusedBatchRstMapper.checkTodayUnusedExists("NCP", candidate.getResourceId());
        if (unusedCount > 0) {
            log.info("Skipping Recommend: Instance already processed by Unused Job today. instanceNo={}", candidate.getResourceId());
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
                log.info("Skipping: recommended spec is same as current. instanceNo={}, spec={}",
                    candidate.getResourceId(), recommendSpecName);
                return null;
            }

            Double monthlySavings = null;
            if ("Down".equals(candidate.getRecommendType())) {
                boolean currentMissing   = candidate.getCurrentCostPerHour()   == null;
                boolean recommendMissing = candidate.getRecommendCostPerHour() == null;

                if (currentMissing && recommendMissing) {
                    log.warn("Down 절감액 계산 불가 - 현재/추천 스펙 단가 모두 미제공, instanceNo: {}", candidate.getResourceId());
                } else if (currentMissing) {
                    log.warn("Down 절감액 계산 불가 - 현재 스펙 단가 미제공, instanceNo: {}", candidate.getResourceId());
                } else if (recommendMissing) {
                    log.warn("Down 절감액 계산 불가 - 추천 스펙 단가 미제공, instanceNo: {}", candidate.getResourceId());
                } else {
                    double savings = (candidate.getCurrentCostPerHour() - candidate.getRecommendCostPerHour()) * 24 * 30;
                    if (savings > 0) monthlySavings = savings;
                }
            }

            result = RecommendVmTypeDto.builder()
                .currentType(candidate.getCurrentSpecName() != null
                    ? candidate.getCurrentSpecName()
                    : candidate.getServerSpecCode())
                .recommendType(recommendSpecName)
                .usd(monthlySavings)
                .build();

        } else if ("Modernize".equals(candidate.getRecommendType())) {
            // Modernize: Tumblebug 다음 세대 스펙 조회
            String modernizeSpecName = tumblebugClient.findModernizeSpec(candidate);
            if (modernizeSpecName == null) {
                log.warn("No modernize recommendation from Tumblebug for instanceNo={}, currentType={}",
                    candidate.getResourceId(), candidate.getServerSpecCode());
                return null;
            }

            if (modernizeSpecName.equals(candidate.getServerSpecCode())) {
                log.info("Modernize skipped: recommended spec same as current. instanceNo={}, spec={}",
                    candidate.getResourceId(), modernizeSpecName);
                return null;
            }

            result = RecommendVmTypeDto.builder()
                .currentType(candidate.getServerSpecCode())
                .recommendType(modernizeSpecName)
                .build();

        } else {
            log.warn("Unknown recommend type: {}", candidate.getRecommendType());
            return null;
        }

        // DTO 조립
        result.setMemberNo(candidate.getMemberNo());
        result.setVmId(candidate.getResourceId());
        result.setProjectCd(candidate.getTbbNsId());  // 후보 쿼리가 csp_instanceid 조인으로 구한 service_cd (=system)
        if (result.getCurrentType() == null) {
            result.setCurrentType(candidate.getServerSpecCode());
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
