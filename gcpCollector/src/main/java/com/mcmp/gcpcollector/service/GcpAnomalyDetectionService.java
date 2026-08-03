package com.mcmp.gcpcollector.service;

import com.mcmp.gcpcollector.alarm.GcpAlarmSender;
import com.mcmp.gcpcollector.dao.GcpAnomalyDao;
import com.mcmp.gcpcollector.dto.AlarmHistoryDto;
import com.mcmp.gcpcollector.dto.GcpAnomalyDto;
import com.mcmp.gcpcollector.dto.GcpVmCostAnalysisDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpAnomalyDetectionService {

    private static final String CSP_TYPE = "GCP";

    private final GcpAnomalyDao gcpAnomalyDao;
    private final GcpAlarmSender gcpAlarmSender;

    public void detect() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<String> subjectDTs = getLastMonthSameDayOfWeekDates(yesterday);

        log.info("GCP 이상비용 탐지 시작 - 기준일: {}, 비교 날짜: {}", yesterday, subjectDTs);

        if (subjectDTs.isEmpty()) {
            log.warn("지난달 같은 요일 데이터가 없습니다.");
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("standardDT", yesterday.toString());
        param.put("subjectDTs", subjectDTs);

        List<GcpVmCostAnalysisDto> vmResults = gcpAnomalyDao.getGcpVmAbnormalCosts(param);

        if (vmResults.isEmpty()) {
            log.info("VM 단위로 매핑된 GCP 빌링 데이터가 없습니다.");
        } else {
            log.info("GCP 이상비용 탐지 대상 VM 수: {}", vmResults.size());

            for (GcpVmCostAnalysisDto item : vmResults) {
                try {
                    processVm(item);
                } catch (Exception e) {
                    log.error("VM 이상비용 탐지 실패 - vmId: {}", item.getVmId(), e);
                }
            }
        }

        log.info("GCP 이상비용 탐지 완료");
    }

    private void processVm(GcpVmCostAnalysisDto item) {
        if (item.getAvgCost() == null || item.getAvgCost() == 0) {
            log.info("지난달 같은 요일 기준 데이터 없음 - 탐지 스킵 - vmId: {}", item.getVmId());
            return;
        }

        double percentagePoint = calcPercentagePoint(item.getLatestCost(), item.getAvgCost());
        String rating = getAnomalyRating(percentagePoint);

        log.debug("vmId: {}, latestCost: {}, avgCost: {}, 변화율: {}%, 등급: {}",
                item.getVmId(), item.getLatestCost(), item.getAvgCost(),
                String.format("%.2f", percentagePoint), rating);

        if (rating == null) return;

        GcpAnomalyDto anomalyDto = GcpAnomalyDto.builder()
                .collectDt(LocalDateTime.now())
                .vmId(item.getVmId())
                .productCd(item.getVmId())
                .abnormalRating(rating)
                .percentagePoint(percentagePoint)
                .standardCost(item.getLatestCost())
                .subjectCost(item.getAvgCost())
                .projectCd(item.getProjectCd())
                .workspaceCd(item.getWorkspaceCd())
                .cspType(CSP_TYPE)
                .build();

        sendVmAlarm(anomalyDto, item);
        gcpAnomalyDao.insertDailyAbnormal(anomalyDto);

        log.info("VM 이상비용 탐지 - vmId: {}, 등급: {}, 변화율: {}%",
                item.getVmId(), rating, String.format("%.2f", percentagePoint));
    }

    private void sendVmAlarm(GcpAnomalyDto anomalyDto, GcpVmCostAnalysisDto item) {
        String note = String.format(
                "GCP VM (%s) cost has increased compared to last month's same-weekday average (%.2f) by %.2f%%. (current: %.2f)",
                item.getVmId(),
                anomalyDto.getSubjectCost(),
                anomalyDto.getPercentagePoint(),
                anomalyDto.getStandardCost()
        );
        gcpAlarmSender.send(AlarmHistoryDto.builder()
                .eventType("Abnormal")
                .resourceId(item.getVmId())
                .resourceType("GCP VM")
                .accountId(item.getBillingAccountId())
                .urgency(anomalyDto.getAbnormalRating())
                .plan(anomalyDto.getAbnormalRating())
                .note(note)
                .projectCd(anomalyDto.getProjectCd()));
    }

    /**
     * 어제와 같은 요일인 지난달 날짜 목록 반환
     * ex) 어제가 목요일이면 지난달의 모든 목요일 날짜 반환
     */
    private List<String> getLastMonthSameDayOfWeekDates(LocalDate yesterday) {
        DayOfWeek dayOfWeek = yesterday.getDayOfWeek();
        LocalDate lastMonthSameDate = yesterday.minusMonths(1);
        LocalDate firstDay = lastMonthSameDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay  = lastMonthSameDate.with(TemporalAdjusters.lastDayOfMonth());

        List<String> result = new ArrayList<>();
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == dayOfWeek) {
                result.add(date.toString());
            }
        }
        return result;
    }

    private double calcPercentagePoint(Double standardCost, Double subjectCost) {
        if (standardCost == null || subjectCost == null || subjectCost == 0) return 0.0;
        return ((standardCost - subjectCost) / subjectCost) * 100;
    }

    private String getAnomalyRating(double percentagePoint) {
        if (percentagePoint >= 30) return "Critical";
        if (percentagePoint >= 20) return "Caution";
        if (percentagePoint >= 10) return "Warning";
        return null;
    }
}
