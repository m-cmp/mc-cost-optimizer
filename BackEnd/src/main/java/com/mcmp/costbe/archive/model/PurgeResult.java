package com.mcmp.costbe.archive.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** raw 삭제 결과. */
@Data
@AllArgsConstructor
public class PurgeResult {
    private Csp     csp;
    private String  yearMonth;
    private boolean success;
    private long    deletedRows;
    private String  message;

    public static PurgeResult success(Csp csp, String ym, long rows) {
        return new PurgeResult(csp, ym, true, rows, null);
    }
    public static PurgeResult failed(Csp csp, String ym, String reason) {
        return new PurgeResult(csp, ym, false, 0, reason);
    }
}
