package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.dto.RecommendCandidateDto;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendVmTypeDto;
import com.mcmp.ncp.vm.rightsizer.mapper.NcpRightSizeMapper;
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

    private final NcpRightSizeMapper ncpRightSizeMapper;
    private final UnusedBatchRstMapper unusedBatchRstMapper;

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

        if ("Up".equals(candidate.getRecommendType())) {
            // SizeUp: Mapper 직접 호출
            result = ncpRightSizeMapper.getRecommendSizeUpVmType(
                candidate.getRegionCode(),
                candidate.getServerSpecCode()
            );
        } else if ("Down".equals(candidate.getRecommendType())) {
            // SizeDown: Mapper 직접 호출
            result = ncpRightSizeMapper.getRecommendSizeDownVmType(
                candidate.getRegionCode(),
                candidate.getServerSpecCode(),
                0.5  // discountRate
            );
        } else if ("Modernize".equals(candidate.getRecommendType())) {
            // Modernize: G3 세대로 업그레이드
            result = ncpRightSizeMapper.getRecommendModernizeVmType(
                candidate.getRegionCode(),
                candidate.getServerSpecCode()
            );
        } else {
            log.warn("Unknown recommend type: {}", candidate.getRecommendType());
            return null;
        }

        if (result == null) {
            log.warn("No recommendation found for resourceId={}, currentType={}",
                candidate.getResourceId(),
                candidate.getServerSpecCode());
            return null;
        }

        // Modernize: 추천 VM이 기존 VM과 동일하면 알림 보내지 않음
        if ("Modernize".equals(candidate.getRecommendType())) {
            if (result.getRecommendType() != null &&
                result.getRecommendType().equals(candidate.getServerSpecCode())) {
                log.info("Modernize skipped: Recommended VM type is same as current type. vmId={}, currentType={}",
                    candidate.getResourceId(),
                    candidate.getServerSpecCode());
                return null;
            }
        }

        // DTO 조립
        result.setMemberNo(candidate.getMemberNo());
        result.setVmId(candidate.getResourceId());
        result.setCurrentType(candidate.getServerSpecCode());
        result.setPlan(candidate.getRecommendType());  // "UP" or "DOWN"

        log.info("Recommendation generated: vmId={}, currentType={}, recommendType={}, plan={}",
            result.getVmId(),
            result.getCurrentType(),
            result.getRecommendType(),
            result.getPlan());

        return result;
    }
}
