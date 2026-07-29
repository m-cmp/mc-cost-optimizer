package com.mcmp.costbe.archive.service;

import com.mcmp.costbe.archive.dao.ArchiveDao;
import com.mcmp.costbe.archive.dao.ArchiveManifestDao;
import com.mcmp.costbe.archive.model.ArchiveManifest;
import com.mcmp.costbe.archive.model.Csp;
import com.mcmp.costbe.archive.model.GateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * raw 삭제 게이트(§6, 콜렉터 재수집 범위 코드 검증 반영).
 *  전부 참이어야 삭제 허용:
 *   1) manifest.status == VERIFIED
 *   2) CSP별 확정 신호:
 *      - AWS  : cur_process_info.certifed_fixed_yn 전부 'Y'(인보이스 발행)
 *      - 그 외: 대상월 < 현재월 AND 월말 후 buffer-days 경과 (콜렉터가 지난 달 재수집 안 함)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PurgeGatePolicy {

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyyMM");

    private final ArchiveManifestDao manifestDao;
    private final ArchiveDao archiveDao;

    @Value("${archive.purge.buffer-days:3}")
    private int bufferDays;

    public GateResult canPurge(String yearMonth, Csp csp) {
        if (csp == null || yearMonth == null || !yearMonth.matches("\\d{6}")) {
            return new GateResult(csp, yearMonth, false, "Invalid request (year-month/CSP)");
        }
        try {
            // 1) 아카이브 검증 완료 여부
            ArchiveManifest m = manifestDao.find(yearMonth, csp.name());
            if (m == null || !"VERIFIED".equals(m.getStatus())) {
                return new GateResult(csp, yearMonth, false, "Archive not completed (not VERIFIED)");
            }
            if (Boolean.TRUE.equals(m.getRawPurged())) {
                return new GateResult(csp, yearMonth, false, "Already deleted");
            }

            // 2) CSP별 확정 신호
            if (csp == Csp.AWS) {
                boolean finalized = archiveDao.isAwsMonthFinalized(yearMonth);
                return finalized
                        ? new GateResult(csp, yearMonth, true, null)
                        : new GateResult(csp, yearMonth, false, "AWS invoice not finalized yet (still subject to re-collection)");
            }

            // GCP/Azure/NCP: 월 종료 + buffer 경과
            if (isEndedWithBuffer(yearMonth)) {
                return new GateResult(csp, yearMonth, true, null);
            }
            return new GateResult(csp, yearMonth, false, "The month has not ended yet, or the closing buffer has not passed");

        } catch (Exception e) {
            // ★ 어떤 오류든 fail-closed(삭제 불가)로 반환 — 500 방지 + 데이터 안전
            log.error("[archive] 게이트 판정 오류 {}/{}: {}", yearMonth, csp, e.getMessage());
            return new GateResult(csp, yearMonth, false, "Gate check error");
        }
    }

    /** 대상월이 끝나고 buffer-days 지났는가 (now >= 익월 1일 + buffer). */
    private boolean isEndedWithBuffer(String yearMonth) {
        try {
            YearMonth target = YearMonth.parse(yearMonth, YM);
            LocalDate threshold = target.plusMonths(1).atDay(1).plusDays(bufferDays);
            return !LocalDate.now().isBefore(threshold);
        } catch (Exception e) {
            log.warn("[archive] 게이트 월 파싱 실패: {}", yearMonth);
            return false;
        }
    }
}
