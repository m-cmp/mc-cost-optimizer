package com.mcmp.dummybe.model.dashboard;

import lombok.Data;

import java.util.UUID;

@Data
public class DashboardUserDataModel {
    private UUID uuid;
    private String siteId;
    private String companyId;
    private String userId;
    private Integer dashboardIndex;
    private Boolean isTemplate;
    private Boolean isDashboardSelected;
    private String dashboardName;
}
