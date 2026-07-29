package com.mcmp.costbe.archive.purger;

import com.mcmp.costbe.archive.model.Csp;
import com.mcmp.costbe.archive.model.PurgeResult;

/** CSP별 raw 삭제 전략(Strategy). CSP 추가 = 구현 1개 추가(기존 무변경, OCP). */
public interface RawPurger {
    Csp csp();
    PurgeResult purge(String yearMonth);
}
