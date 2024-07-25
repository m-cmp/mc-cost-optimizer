package com.mcmp.costselector.unused.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UnusedBatchRstModel {
    private LocalDateTime create_dt;
    private String csp_type;
    private String csp_account;
    private String csp_instanceid;
    private String plan_type;
}
