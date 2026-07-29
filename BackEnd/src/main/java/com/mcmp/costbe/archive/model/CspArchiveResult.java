package com.mcmp.costbe.archive.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** (월 × CSP) 아카이브 1건 결과. */
@Data
@AllArgsConstructor
public class CspArchiveResult {
    private Csp    csp;
    private String status;    // VERIFIED / FAILED / SKIPPED
    private String s3Key;     // 성공 시 전체 키
    private long   rowCount;
    private long   byteSize;
    private String message;   // 실패/스킵 사유

    public static CspArchiveResult verified(Csp csp, String key, long rows, long bytes) {
        return new CspArchiveResult(csp, "VERIFIED", key, rows, bytes, null);
    }
    public static CspArchiveResult failed(Csp csp, String reason) {
        return new CspArchiveResult(csp, "FAILED", null, 0, 0, reason);
    }
    public static CspArchiveResult skipped(Csp csp, String reason) {
        return new CspArchiveResult(csp, "SKIPPED", null, 0, 0, reason);
    }
}
