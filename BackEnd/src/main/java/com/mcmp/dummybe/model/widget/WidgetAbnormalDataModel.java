package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class WidgetAbnormalDataModel {
    private BigDecimal totalIncreaseDecreaseCost;
    private BigDecimal totalIncreaseDecreaseRate;
    private Date latestSummarizedBillDate;
    private List<WidgetAbnormalListModel> abnormalList;
}
