package com.mcmp.costbe.archive.purger;

import com.mcmp.costbe.archive.model.PurgeResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 삭제 공통 절차(Template Method): 검증 → deletePartition → 결과/로그.
 * 하위는 CSP별 실제 DELETE/DROP 인 deletePartition 만 구현.
 */
@Slf4j
public abstract class AbstractRawPurger implements RawPurger {

    @Override
    public final PurgeResult purge(String yearMonth) {
        if (yearMonth == null || !yearMonth.matches("\\d{6}")) {   // 삼중 방어(게이트/여기/DAO)
            return PurgeResult.failed(csp(), yearMonth, "잘못된 연월");
        }
        try {
            long rows = deletePartition(yearMonth);
            log.info("[archive] raw 삭제 {}/{} - {} rows", yearMonth, csp(), rows);
            return PurgeResult.success(csp(), yearMonth, rows);
        } catch (Exception e) {
            log.error("[archive] raw 삭제 실패 {}/{}: {}", yearMonth, csp(), e.getMessage(), e);
            return PurgeResult.failed(csp(), yearMonth, e.getMessage());
        }
    }

    /** CSP별 실제 삭제. 삭제된 행수 반환(DROP 등 미집계분은 제외될 수 있음). */
    protected abstract long deletePartition(String yearMonth);
}
