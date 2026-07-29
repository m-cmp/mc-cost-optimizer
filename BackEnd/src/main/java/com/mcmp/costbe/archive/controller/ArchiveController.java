package com.mcmp.costbe.archive.controller;

import com.mcmp.costbe.archive.model.*;
import com.mcmp.costbe.archive.service.CostArchiveFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 비용 인보이스 아카이빙 API. 컨트롤러는 Facade 만 의존(Facade 패턴).
 * (raw 삭제 POST /purge 는 Phase 4)
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/costopti/be/archive")
@Tag(name = "Cost Archive", description = "비용 인보이스 아카이빙 (버킷 저장 / 현황 / 삭제 게이트)")
public class ArchiveController {

    private final CostArchiveFacade facade;

    @GetMapping("/months")
    @Operation(summary = "아카이브 가능한 연·월 목록")
    public List<String> months() {
        return facade.archivableMonths();
    }

    @GetMapping("/status")
    @Operation(summary = "아카이브 현황(manifest 전체)")
    public List<ArchiveManifest> status() {
        return facade.getStatus();
    }

    @GetMapping("/gate")
    @Operation(summary = "raw 삭제 게이트 판정")
    public GateResult gate(@RequestParam String yearMonth, @RequestParam Csp csp) {
        return facade.evaluatePurgeGate(yearMonth, csp);
    }

    @PostMapping
    @Operation(summary = "인보이스 아카이브(비파괴, 삭제 안 함)")
    public ArchiveResult archive(@RequestBody ArchiveOptions opts) {
        return facade.archiveInvoice(opts);
    }

    @PostMapping("/purge")
    @Operation(summary = "raw 삭제(게이트 통과 시만, 아카이브와 별개)")
    public PurgeResult purge(@RequestBody PurgeReqModel req) {
        return facade.purgeRaw(req.getYearMonth(), req.getCsp());
    }
}
