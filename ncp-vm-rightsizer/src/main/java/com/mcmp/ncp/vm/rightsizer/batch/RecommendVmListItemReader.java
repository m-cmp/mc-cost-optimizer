package com.mcmp.ncp.vm.rightsizer.batch;

import com.mcmp.ncp.vm.rightsizer.dto.RecommendCandidateDto;
import com.mcmp.ncp.vm.rightsizer.mapper.UnusedProcessMartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import java.util.Iterator;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class RecommendVmListItemReader implements ItemReader<RecommendCandidateDto> {

    private final UnusedProcessMartMapper unusedProcessMartMapper;
    private Iterator<RecommendCandidateDto> candidateIterator;

    @Override
    public RecommendCandidateDto read() {
        if (candidateIterator == null) {
            // 4일간 메트릭 데이터 기반 SizeUp/Down 추천 대상 조회
            List<RecommendCandidateDto> candidates = unusedProcessMartMapper.selectRecommendCandidates();
            candidateIterator = candidates.iterator();
            log.info("Recommend VM candidates loaded: {} items", candidates.size());
        }

        if (candidateIterator.hasNext()) {
            RecommendCandidateDto candidate = candidateIterator.next();
            log.debug("Reading candidate: resourceId={}, type={}, avgCpu={}, maxCpu={}",
                candidate.getResourceId(),
                candidate.getRecommendType(),
                candidate.getAvg4DaysCpu(),
                candidate.getMax4DaysCpu());
            return candidate;
        }

        return null;
    }
}
