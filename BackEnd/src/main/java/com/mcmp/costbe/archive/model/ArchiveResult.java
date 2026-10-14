package com.mcmp.costbe.archive.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** 아카이브 요청 결과 (월 단위, CSP별 결과 목록). */
@Data
@AllArgsConstructor
public class ArchiveResult {
    private String yearMonth;
    private List<CspArchiveResult> results;
}
