package com.mcmp.dummybe.model.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDataModel {
    private Map<String, String> batchTime;
    private List<String> widgetType;
    private Object widgetValues;
    private Map<String, DashboardWidgetDefaultValuesModel> widgetDefaultValue;
    private UserData userData;

    public DashboardDataModel(){
        Map<String,String> batchTime = new HashMap<>();
        batchTime.put("TENCENT", "2023-02-02 12:00:13");
        batchTime.put("AZURE", "2023-08-31 19:26:51");
        batchTime.put("OCI", "2023-09-01T03:28:22.0");
        batchTime.put("GCP", "2023-09-01 01:28:25");
        batchTime.put("NCP", "2023-08-31");
        batchTime.put("AWS", "2023-08-31T23:45:34.0");

        this.batchTime = batchTime;
    }

    @Data
    public static class UserData {
        private List<DashboardData> dashboardData;

        public UserData(){
            this.dashboardData = new ArrayList<>();
        }
        @Data
        public static class DashboardData {
            private String siteCode;
            private String companyId;
            private String userId;
            private Integer index;
            private Boolean isTemplate;
            private Boolean isDashboardSelected;
            private String dashboardName;
            private List<UserWidget> widgets;

        }
    }
}
