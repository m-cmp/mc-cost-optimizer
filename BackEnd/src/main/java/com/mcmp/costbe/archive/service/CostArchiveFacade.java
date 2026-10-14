package com.mcmp.costbe.archive.service;

import com.mcmp.costbe.archive.model.ArchiveManifest;
import com.mcmp.costbe.archive.model.ArchiveOptions;
import com.mcmp.costbe.archive.model.ArchiveResult;
import com.mcmp.costbe.archive.model.Csp;
import com.mcmp.costbe.archive.model.GateResult;
import com.mcmp.costbe.archive.model.PurgeResult;

import java.util.List;

/**
 * 비용 인보이스 아카이빙 진입점(Facade). 컨트롤러는 이 인터페이스만 의존한다.
 */
public interface CostArchiveFacade {

    /** 인보이스 아카이브(비파괴). 한 요청 = 한 달 × 선택 CSP. 삭제하지 않음. */
    ArchiveResult archiveInvoice(ArchiveOptions opts);

    /** 현황(manifest 전체). 프론트가 월×CSP 매트릭스로 렌더. */
    List<ArchiveManifest> getStatus();

    /** 아카이브 가능한 연·월(각 CSP 소스 합집합, 최신순). */
    List<String> archivableMonths();

    /** raw 삭제 게이트 판정(사유 포함). */
    GateResult evaluatePurgeGate(String yearMonth, Csp csp);

    /** raw 삭제(아카이브와 별개). ★ 내부에서 게이트 재확인 후에만 실행. */
    PurgeResult purgeRaw(String yearMonth, Csp csp);
}
