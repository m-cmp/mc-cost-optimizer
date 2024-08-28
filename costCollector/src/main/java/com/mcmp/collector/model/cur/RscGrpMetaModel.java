package com.mcmp.collector.model.cur;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RscGrpMetaModel {
    private String csp;
    private String account;
    private String prj_cd;
    private String workspace_cd;
    private String year_month;
}
