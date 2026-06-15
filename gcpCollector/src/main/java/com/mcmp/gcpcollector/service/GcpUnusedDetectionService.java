package com.mcmp.gcpcollector.service;

import com.mcmp.gcpcollector.alarm.GcpAlarmSender;
import com.mcmp.gcpcollector.dao.GcpUnusedDao;
import com.mcmp.gcpcollector.dto.AlarmHistoryDto;
import com.mcmp.gcpcollector.dto.GcpUnusedVmDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpUnusedDetectionService {

    private static final String RESOURCE_TYPE = "GCP VM";
    private static final double AVG_THRESHOLD = 1.0;   // 14일 평균 CPU < 1%
    private static final double MAX_THRESHOLD = 3.0;   // 14일 최대 CPU < 3%
    private static final int    MIN_DAYS      = 14;

    private final GcpUnusedDao   gcpUnusedDao;
    private final GcpAlarmSender gcpAlarmSender;

    public void detect() {
        log.info("GCP 미사용 자원 탐지 시작");

        List<GcpUnusedVmDto> vmList = gcpUnusedDao.selectYesterdayAvgCpuByVm();

        if (vmList == null || vmList.isEmpty()) {
            log.info("어제 수집된 GCP VM 메트릭 데이터가 없습니다.");
            return;
        }

        log.info("GCP VM 메트릭 대상 수: {}", vmList.size());

        int unusedCount = 0;
        for (GcpUnusedVmDto vm : vmList) {
            try {
                if (processVm(vm)) unusedCount++;
            } catch (Exception e) {
                log.error("GCP VM 미사용 탐지 실패 - vmId: {}", vm.getVmId(), e);
            }
        }

        log.info("GCP 미사용 자원 탐지 완료 - 미사용 판정: {}건", unusedCount);
    }

    /**
     * VM 1개 처리
     * 1. unused_daily_mart에 어제 평균 CPU 저장
     * 2. 14일 통계 조회 및 미사용 판정
     * 3. Unused이면 unused_batch_rst 저장 + 알람 발송
     *
     * @return true if detected as unused
     */
    private boolean processVm(GcpUnusedVmDto vm) {
        // 1. 어제 평균 CPU 누적 저장
        gcpUnusedDao.insertUnusedDailyMart(vm);
        log.debug("unused_daily_mart 저장 - vmId: {}, date: {}, avgCpu: {}%",
                vm.getVmId(), vm.getYesterdayDate(), vm.getYesterdayAvgCpu());

        // 2. 14일 통계 조회
        GcpUnusedVmDto stats = gcpUnusedDao.select14DaysMetricStats(vm.getVmId());

        if (stats == null) {
            log.debug("14일 통계 없음 - 스킵 - vmId: {}", vm.getVmId());
            return false;
        }
        if (stats.getDayCount() == null || stats.getDayCount() < MIN_DAYS) {
            log.debug("14일 데이터 부족 - 스킵 - vmId: {}, 수집일수: {}",
                    vm.getVmId(), stats.getDayCount());
            return false;
        }
        if (stats.getAvgCpu14Days() == null || stats.getMaxCpu14Days() == null) {
            log.debug("메트릭 값 null - 스킵 - vmId: {}", vm.getVmId());
            return false;
        }

        vm.setAvgCpu14Days(stats.getAvgCpu14Days());
        vm.setMaxCpu14Days(stats.getMaxCpu14Days());
        vm.setDayCount(stats.getDayCount());

        // 3. 미사용 판정: 14일 평균 < 1% OR 14일 최대 < 3%
        boolean isUnused = vm.getAvgCpu14Days() < AVG_THRESHOLD
                        || vm.getMaxCpu14Days() < MAX_THRESHOLD;

        if (!isUnused) {
            log.debug("정상 사용 중 - vmId: {}, avg: {}%, max: {}%",
                    vm.getVmId(), vm.getAvgCpu14Days(), vm.getMaxCpu14Days());
            return false;
        }

        vm.setUnusedRating("Unused");
        log.info("미사용 자원 탐지 - vmId: {}, avg: {}%, max: {}%, 수집일수: {}",
                vm.getVmId(),
                String.format("%.2f", vm.getAvgCpu14Days()),
                String.format("%.2f", vm.getMaxCpu14Days()),
                vm.getDayCount());

        // 4. unused_batch_rst 저장
        try {
            gcpUnusedDao.insertUnusedBatchRst(vm);
        } catch (Exception e) {
            log.error("unused_batch_rst 저장 실패 - vmId: {}", vm.getVmId(), e);
        }

        // 5. 알람 발송
        sendAlarm(vm);
        return true;
    }

    private void sendAlarm(GcpUnusedVmDto vm) {
        String note = String.format(
                "GCP VM(%s)의 지난 14일간 CPU 사용률이 매우 낮습니다. (평균: %.2f%%, 최대: %.2f%%) " +
                "미사용 자원일 수 있으니 확인이 필요합니다.",
                vm.getVmId(), vm.getAvgCpu14Days(), vm.getMaxCpu14Days()
        );
        gcpAlarmSender.send(AlarmHistoryDto.builder()
                .eventType("Unused")
                .resourceId(vm.getVmId())
                .resourceType(RESOURCE_TYPE)
                .accountId(vm.getBillingAccountId())
                .urgency("Warning")
                .plan("Warning")
                .note(note)
                .projectCd(vm.getProjectCd()));
    }
}
