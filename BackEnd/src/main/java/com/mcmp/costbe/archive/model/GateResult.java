package com.mcmp.costbe.archive.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/** raw 삭제 게이트 판정 결과. canPurge=false 면 reason 에 사유. */
@Data
@AllArgsConstructor
public class GateResult {
    private Csp     csp;
    private String  yearMonth;
    private boolean canPurge;
    private String  reason;
}
