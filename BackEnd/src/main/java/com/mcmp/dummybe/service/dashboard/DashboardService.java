package com.mcmp.dummybe.service.dashboard;


import com.mcmp.dummybe.dao.DashboardDAO;
import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.model.dashboard.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private DashboardDAO dashboardDAO;

    /**
     * 사용자의 위젯 및 사용가능한 위젯에 대한 설정 등을 불러옴
     */
    public ResultModel dashboardsDefine(){
        ResultModel result = new ResultModel();

        DashboardDataModel dashboards = new DashboardDataModel();

        List<String> widgetType = dashboardDAO.selectDashboardKinds();
        Map<String, DashboardDataWidgetValues> widgetValues = availableOptByWidget();

        List<DashboardWidgetDefaultValuesModel> defaultOption = dashboardDAO.selectWidgetOptDefault();
        Map<String, DashboardWidgetDefaultValuesModel> widgetDefaultValue = defaultOption.stream()
                .collect(Collectors.toMap(
                        DashboardWidgetDefaultValuesModel::getWidgetType,
                        model -> {
                            model.setWidgetType(null); // or model.setWidgetType(""); depending on your requirement
                            return model;
                        }
                ));

        //userData
        DashboardUserDataModel userDashboard = dashboardDAO.selectUserWidgetInfo(0);
        DashboardDataModel.UserData.DashboardData dashboardData = new DashboardDataModel.UserData.DashboardData();
        dashboardData.setSiteCode(userDashboard.getSiteId());
        dashboardData.setCompanyId(userDashboard.getCompanyId());
        dashboardData.setUserId(userDashboard.getUserId());
        dashboardData.setIndex(userDashboard.getDashboardIndex());
        dashboardData.setIsTemplate(userDashboard.getIsTemplate());
        dashboardData.setIsDashboardSelected(userDashboard.getIsDashboardSelected());
        dashboardData.setDashboardName(userDashboard.getDashboardName());

        List<UserWidget> userWidgets = new ArrayList<>();
        List<DashboardWidgetUserDataModel> rawUserWidgets = dashboardDAO.selectUserWidgetDetailData(userDashboard.getDashboardIndex());
        rawUserWidgets.stream().forEach(item -> {
            UserWidget widget;
            if (item.getWidgetType().equals("dashboard_abnormal_change_widget")){
                AbnormalUserWidget abnormalUserWidget = new AbnormalUserWidget();
                widget = abnormalUserWidget;
            } else{
                widget = new UserWidget();
            }
            widget.setDashboardIndex(item.getDashboardIndex());
            widget.setIndex(item.getWidgetIndex());
            widget.setX(item.getX());
            widget.setY(item.getY());
            widget.setWidth(item.getWidth());
            widget.setHeight(item.getHeight());
            widget.setWidgetType(item.getWidgetType());
            widget.setViewBy(item.getViewBy());
            widget.setDateType(item.getDateType());
            widget.setTimeFrame(item.getTimeFrame());
            widget.setChartType(item.getChartType());
            widget.setScale(item.getScale());
            widget.setFilter(item.getFilter());
            widget.setSelectedAccount(item.getSelectedAccount());
            widget.setUseYn(item.getUseYn());
            widget.setSelectedVendorsByWidget(Arrays.asList(item.getSelectedVendorsByWidget()));
            widget.setIsAbnormalNotiOn(item.getIsAbnormalNotiOn());
//            if(widget instanceof AbnormalUserWidget){
//                AbnormalUserWidget abnormalUserWidget = (AbnormalUserWidget) widget;
//                userWidgets.add(abnormalUserWidget);
//            } else {
//                userWidgets.add(widget);
//            }

            userWidgets.add(widget);

        });
        dashboardData.setWidgets(userWidgets);
        DashboardDataModel.UserData userData = new DashboardDataModel.UserData();
        userData.getDashboardData().add(dashboardData);


        dashboards.setWidgetType(widgetType);
        dashboards.setWidgetValues(widgetValues);
        dashboards.setWidgetDefaultValue(widgetDefaultValue);
        dashboards.setUserData(userData);

        result.setData(dashboards);


        return result;
    }
    private Map<String,DashboardDataWidgetValues> availableOptByWidget(){
//        DashboardDataWidgetValues widgetAvailableOptions = new DashboardDataWidgetValues();
        Map<String,DashboardDataWidgetValues> widgetValues = new HashMap<>();

        List<DashboardWidgetOptionsModel> widgetOpt = dashboardDAO.selectWidgetAvailableOpt();
        widgetOpt.stream()
                .forEach(item -> {
                    DashboardDataWidgetValues widgetAvailableOptions = new DashboardDataWidgetValues();
                    widgetAvailableOptions.setFilter(optionStringSplit(item.getFilter()));
                    widgetAvailableOptions.setDateType(optionStringSplit(item.getDateType()));
                    widgetAvailableOptions.setChartType(optionStringSplit(item.getChartType()));
                    widgetAvailableOptions.setScale(optionStringSplit(item.getScale()));
                    if(item.getWidgetType().equals("dashboard_cost_by_widget")){
                        widgetAvailableOptions.setTimeFrame(parseMonthlyWeekly(item.getTimeFrame()));
                    } else {
                        widgetAvailableOptions.setTimeFrame(optionStringSplit(item.getTimeFrame()));
                    }
                    widgetAvailableOptions.setViewBy(optionStringSplit(item.getViewBy()));

                    if(item.getThreshold() != null){
                        widgetAvailableOptions.setThreshold(optionStringSplit(item.getThreshold()).stream()
                                .map(Integer::parseInt).collect(Collectors.toList()));
                    }

                    widgetValues.put(item.getWidgetType(), widgetAvailableOptions);
                });
        return widgetValues;
    }
    private List<String> optionStringSplit(String splitTarget){
        if(splitTarget != null){
            return Arrays.stream(splitTarget.split("\\|\\|"))
                    .collect(Collectors.toList());
        } else {
            return null ;
        }
    }

    private Map<String, List<String>> parseMonthlyWeekly(String target){
        String [] parseMonthlyWeekly = target.split(",");
        List<String> monthly = optionStringSplit(parseMonthlyWeekly[0]);
        List<String> weekly = optionStringSplit(parseMonthlyWeekly[1]);

        Map<String, List<String>> result = new HashMap<>();
        result.put("monthly", monthly);
        result.put("weekly", weekly);
        return result;

    }
}
