package com.mcmp.costbe.archive.service;

import com.mcmp.costbe.archive.dao.ArchiveDao;
import com.mcmp.costbe.archive.dao.ArchiveManifestDao;
import com.mcmp.costbe.archive.model.*;
import com.mcmp.costbe.archive.purger.RawPurger;
import com.mcmp.costbe.archive.purger.RawPurgerRegistry;
import com.mcmp.costbe.archive.s3.S3ArchiveClient;
import com.mcmp.costbe.invoice.model.InvoiceItemModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 아카이브 오케스트레이션.
 * ★ 과금 방어: 재시도 루프 없음(실패=FAILED 기록만), 한 요청 = 한 달 × 선택 CSP(≤4)만.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CostArchiveFacadeImpl implements CostArchiveFacade {

    private final InvoiceQueryService invoiceQueryService;
    private final CsvSerializer csvSerializer;
    private final S3ArchiveClient s3;
    private final ArchiveManifestDao manifestDao;
    private final ArchiveDao archiveDao;
    private final PurgeGatePolicy purgeGatePolicy;
    private final RawPurgerRegistry rawPurgerRegistry;

    @Override
    public ArchiveResult archiveInvoice(ArchiveOptions opts) {
        String ym = opts != null ? opts.getYearMonth() : null;
        List<CspArchiveResult> results = new ArrayList<>();

        if (ym == null || !ym.matches("\\d{6}")) {
            log.warn("[archive] 잘못된 yearMonth: {}", ym);
            return new ArchiveResult(ym, results);
        }

        // 대상 CSP: 비면 전체(≤4). 여러 달 순회는 애초에 불가(단일 ym).
        List<Csp> csps = (opts.getCsps() == null || opts.getCsps().isEmpty())
                ? Arrays.asList(Csp.values())
                : opts.getCsps();

        for (Csp csp : csps) {
            results.add(archiveOne(ym, csp, opts.getProjects()));   // 재시도 없음, CSP별 격리
        }
        return new ArchiveResult(ym, results);
    }

    @Override
    public List<ArchiveManifest> getStatus() {
        try {
            List<ArchiveManifest> all = manifestDao.findAll();
            return all != null ? all : Collections.emptyList();
        } catch (Exception e) {
            log.error("[archive] 현황 조회 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> archivableMonths() {
        try {
            List<String> months = archiveDao.getArchivableMonths();
            return months != null ? months : Collections.emptyList();
        } catch (Exception e) {
            log.error("[archive] 월 목록 조회 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public GateResult evaluatePurgeGate(String yearMonth, Csp csp) {
        return purgeGatePolicy.canPurge(yearMonth, csp);
    }

    @Override
    public PurgeResult purgeRaw(String yearMonth, Csp csp) {
        if (csp == null) {
            return PurgeResult.failed(null, yearMonth, "CSP 없음");
        }
        // ★ 호출자를 신뢰하지 않고 게이트 재확인 (아카이브 VERIFIED + CSP별 확정 신호)
        GateResult gate = purgeGatePolicy.canPurge(yearMonth, csp);
        if (!gate.isCanPurge()) {
            log.warn("[archive] purge 거부 {}/{}: {}", yearMonth, csp, gate.getReason());
            return PurgeResult.failed(csp, yearMonth, "게이트 거부: " + gate.getReason());
        }
        RawPurger purger = rawPurgerRegistry.get(csp);
        if (purger == null) {
            return PurgeResult.failed(csp, yearMonth, "지원하지 않는 CSP");
        }
        PurgeResult r = purger.purge(yearMonth);
        if (r.isSuccess()) {
            manifestDao.markPurged(yearMonth, csp.name());
            log.info("[archive] Purged raw {}/{} ({} rows)", yearMonth, csp, r.getDeletedRows());
        }
        return r;
    }

    /** (월 × CSP) 1건 아카이브: fetch → csv → put → head 검증 → manifest. 실패는 격리. */
    private CspArchiveResult archiveOne(String ym, Csp csp, List<String> projects) {
        try {
            List<InvoiceItemModel> rows = invoiceQueryService.fetchInvoice(ym, csp, projects);
            if (rows.isEmpty()) {
                log.info("[archive] {}/{} 데이터 없음 — 스킵", ym, csp);
                return CspArchiveResult.skipped(csp, "NO_DATA");
            }

            SerializedCsv csv = csvSerializer.toCsvGz(rows);
            String relKey = "year_month=" + ym + "/invoice_" + ym + "_" + csp.name() + ".csv.gz";

            PutResult put = s3.put(relKey, csv.getGz());
            if (put == null) {
                manifestDao.upsert(manifest(ym, csp, null, csv, "FAILED"));
                return CspArchiveResult.failed(csp, "S3_PUT_FAILED");
            }

            // 검증: 존재 + 크기 일치
            HeadResult head = s3.head(relKey);
            if (head == null || !head.isExists() || head.getByteSize() != csv.getByteSize()) {
                manifestDao.upsert(manifest(ym, csp, put.getKey(), csv, "FAILED"));
                log.error("[archive] {}/{} 검증 실패 (head={})", ym, csp, head);
                return CspArchiveResult.failed(csp, "VERIFY_FAILED");
            }

            manifestDao.upsert(manifest(ym, csp, put.getKey(), csv, "VERIFIED"));
            log.info("[archive] Archived {}/{} → s3://{}/{} ({} rows, {} bytes)",
                    ym, csp, put.getBucket(), put.getKey(), csv.getRowCount(), csv.getByteSize());
            return CspArchiveResult.verified(csp, put.getKey(), csv.getRowCount(), csv.getByteSize());

        } catch (Exception e) {
            log.error("[archive] {}/{} 아카이브 오류: {}", ym, csp, e.getMessage(), e);
            try { manifestDao.upsert(manifest(ym, csp, null, null, "FAILED")); } catch (Exception ignore) {}
            return CspArchiveResult.failed(csp, e.getMessage());
        }
    }

    private ArchiveManifest manifest(String ym, Csp csp, String key, SerializedCsv csv, String status) {
        ArchiveManifest m = new ArchiveManifest();
        m.setYearMonth(ym);
        m.setCsp(csp.name());
        m.setS3Key(key);
        m.setRowCount(csv != null ? csv.getRowCount() : 0L);
        m.setByteSize(csv != null ? csv.getByteSize() : 0L);
        m.setSha256(csv != null ? csv.getSha256() : null);
        m.setStatus(status);
        return m;
    }
}
