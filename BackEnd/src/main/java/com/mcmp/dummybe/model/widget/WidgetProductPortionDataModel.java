package com.mcmp.dummybe.model.widget;

import lombok.Data;

import java.util.List;

@Data
public class WidgetProductPortionDataModel {
    private List<WidgetProductPortionPortionModel> portion;
    private List<WidgetProductPortionAccountModel> accounts;
    private String selectedAccount;
    private String selectedTimeFrame;
    private List<WidgetProductPortionTimeFrameModel> timeFrameList;
    private String latestSummarizedBillDate;


}
