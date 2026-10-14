package com.mcmp.gcpcollector.service;

import com.mcmp.gcpcollector.alarm.GcpAlarmSender;
import com.mcmp.gcpcollector.client.TumblebugClient;
import com.mcmp.gcpcollector.dao.GcpUnusedDao;
import com.mcmp.gcpcollector.dto.AlarmHistoryDto;
import com.mcmp.gcpcollector.dto.GcpVmRightSizeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpRightSizeService {

    private static final String RESOURCE_TYPE = "GCP VM";

    // Size Up: 4일 평균 CPU > 80%
    private static final double UP_AVG_THRESHOLD   = 80.0;
    // Size Down: 4일 평균 CPU <= 10% AND 최대 CPU <= 50%
    private static final double DOWN_AVG_THRESHOLD = 10.0;
    private static final double DOWN_MAX_THRESHOLD = 50.0;
    private static final int    MIN_DAYS           = 4;

    private final GcpUnusedDao   gcpUnusedDao;
    private final TumblebugClient tumblebugClient;
    private final GcpAlarmSender gcpAlarmSender;

    /** 배치 스케줄러 호출 - Up/Down 전체 */
    public void detect() {
        detect(null);
    }

    /**
     * direction: "Up" | "Down" | null(전체)
     */
    public int detect(String direction) {
        log.info("GCP VM 사이즈 최적화 탐지 시작 - direction: {}", direction == null ? "ALL" : direction);

        List<GcpVmRightSizeDto> candidates = gcpUnusedDao.selectRightSizeCandidates();

        if (candidates == null || candidates.isEmpty()) {
            log.info("GCP VM 사이즈 최적화 대상 없음");
            return 0;
        }

        // direction 필터 적용
        if (direction != null) {
            candidates = candidates.stream()
                    .filter(vm -> direction.equalsIgnoreCase(vm.getRecommendType()))
                    .toList();
        }

        log.info("GCP VM 사이즈 최적화 대상: {}건 (direction: {})",
                candidates.size(), direction == null ? "ALL" : direction);

        int processedCount = 0;
        for (GcpVmRightSizeDto vm : candidates) {
            try {
                if (processVm(vm)) processedCount++;
            } catch (Exception e) {
                log.error("GCP VM 사이즈 최적화 처리 실패 - vmId: {}", vm.getVmId(), e);
            }
        }

        log.info("GCP VM 사이즈 최적화 탐지 완료 - 알람 발송: {}건", processedCount);
        return processedCount;
    }

    private boolean processVm(GcpVmRightSizeDto vm) {
        if (vm.getDayCount() == null || vm.getDayCount() < MIN_DAYS) {
            log.debug("데이터 부족 스킵 - vmId: {}, dayCount: {}", vm.getVmId(), vm.getDayCount());
            return false;
        }

        // Unused Job에서 오늘 이미 처리된 VM 제외
        int unusedCount = gcpUnusedDao.checkTodayUnused(vm.getVmId());
        if (unusedCount > 0) {
            log.info("Unused 처리된 VM 스킵 - vmId: {}", vm.getVmId());
            return false;
        }

        // 사이징 방향 결정
        String recommendType = resolveRecommendType(vm);
        if (recommendType == null) {
            log.debug("사이징 대상 아님 - vmId: {}, avg: {}%, max: {}%",
                    vm.getVmId(), vm.getAvg4DaysCpu(), vm.getMax4DaysCpu());
            return false;
        }
        vm.setRecommendType(recommendType);

        // Tumblebug에서 현재 스펙 조회 (Up/Down/Modernize 공통)
        tumblebugClient.fillCurrentSpec(vm);

        String recommendSpecName;
        if ("Modernize".equals(recommendType)) {
            // Modernize: 다음 세대 스펙 존재 여부 확인
            recommendSpecName = tumblebugClient.findModernizeSpec(vm);
            if (recommendSpecName == null) {
                log.info("Modernize 추천 스펙 없음 (다음 세대 미존재) - vmId: {}, spec: {}",
                        vm.getVmId(), vm.getCurrentSpecName());
                return false;
            }
        } else {
            // Up/Down: recommendSpec API 사용
            recommendSpecName = tumblebugClient.recommendSpec(vm);
            if (recommendSpecName == null) {
                log.warn("추천 스펙 없음 - vmId: {}, direction: {}", vm.getVmId(), recommendType);
                return false;
            }
        }

        // 현재 스펙과 동일하면 스킵
        if (recommendSpecName.equals(vm.getCurrentSpecName())) {
            log.info("추천 스펙이 현재와 동일 - vmId: {}, spec: {}", vm.getVmId(), recommendSpecName);
            return false;
        }

        sendAlarm(vm);
        return true;
    }

    private String resolveRecommendType(GcpVmRightSizeDto vm) {
        double avg = vm.getAvg4DaysCpu() != null ? vm.getAvg4DaysCpu() : 0.0;
        double max = vm.getMax4DaysCpu() != null ? vm.getMax4DaysCpu() : 0.0;

        if (avg > UP_AVG_THRESHOLD) return "Up";
        if (avg <= DOWN_AVG_THRESHOLD && max <= DOWN_MAX_THRESHOLD) return "Down";
        return "Modernize";
    }

    private void sendAlarm(GcpVmRightSizeDto vm) {
        String currentSpec   = vm.getCurrentSpecName()   != null ? vm.getCurrentSpecName()   : "unknown";
        String recommendSpec = vm.getRecommendSpecName() != null ? vm.getRecommendSpecName() : "unknown";

        String note = String.format(
                "GCP VM (%s) is recommended for %s sizing based on the past 4 days CPU utilization " +
                "(average: %.1f%%, maximum: %.1f%%). Current spec: %s → recommended spec: %s",
                vm.getVmId(), vm.getRecommendType(),
                vm.getAvg4DaysCpu(), vm.getMax4DaysCpu(),
                currentSpec, recommendSpec);

        if ("Down".equals(vm.getRecommendType())) {
            boolean currentMissing   = vm.getCurrentCostPerHour()   == null;
            boolean recommendMissing = vm.getRecommendCostPerHour() == null;

            if (currentMissing && recommendMissing) {
                log.warn("Down 절감액 계산 불가 - 현재/추천 스펙 단가 모두 미제공, vmId: {}", vm.getVmId());
            } else if (currentMissing) {
                log.warn("Down 절감액 계산 불가 - 현재 스펙 단가 미제공, vmId: {}", vm.getVmId());
            } else if (recommendMissing) {
                log.warn("Down 절감액 계산 불가 - 추천 스펙 단가 미제공, vmId: {}", vm.getVmId());
            } else {
                double monthlySavings = (vm.getCurrentCostPerHour() - vm.getRecommendCostPerHour()) * 24 * 30;
                if (monthlySavings > 0) {
                    note += String.format(" Estimated monthly savings: $%.2f.", monthlySavings);
                }
            }
        }

        gcpAlarmSender.send(AlarmHistoryDto.builder()
                .eventType("Resize")
                .resourceId(vm.getVmId())
                .resourceType(RESOURCE_TYPE)
                .accountId(vm.getBillingAccountId())
                .urgency("Caution")
                .plan(vm.getRecommendType())
                .note(note)
                .projectCd(vm.getProjectCd()));
    }
}
