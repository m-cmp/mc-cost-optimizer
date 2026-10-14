package com.mcmp.costbe.archive.model;

import lombok.Data;

import java.util.List;

/** 아카이브 요청. ★ 한 요청 = 한 달(yearMonth) × 선택 CSP(≤4). 여러 달 순회 금지(과금 방어). */
@Data
public class ArchiveOptions {
    private String yearMonth;        // "202606"
    private List<Csp> csps;          // 대상 CSP (비면 전체)
    private List<String> projects;   // namespace 필터 (비면 전체)
}
