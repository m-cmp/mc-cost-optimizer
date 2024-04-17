package com.mcmp.dummybe.model.widget;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class WidgetCostDataModel {
    private List<Map<String, Object>> costByCondition;
    private List<WidgetCostCustomFilterModel> customFilters;
    private WidgetCostTimeFrameModel timeFrameScope;

    @Data
    @Builder
    public static class WidgetCostTimeFrameModel {
        private Date endDate;
        private Date startDate;
    }
}
