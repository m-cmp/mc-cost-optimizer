package com.mcmp.costbe.archive.model;

import lombok.Data;

/** raw 삭제 요청. */
@Data
public class PurgeReqModel {
    private String yearMonth;
    private Csp    csp;
}
