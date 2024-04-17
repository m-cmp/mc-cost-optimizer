package com.mcmp.dummybe.service.widget;

import com.mcmp.dummybe.dao.widget.WidgetDAO;
import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.model.widget.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WidgetService {
    @Autowired
    private WidgetDAO widgetDAO;

    public ResultModel makeTrendChart(){
        ResultModel result = new ResultModel();
        List<WidgetTrendDailyCostModel> trendCost = widgetDAO.selectTrendDaily();

        Map<String, WidgetTrendMonthlyCostModel> monthlyCostMap = new HashMap<>();

        for (WidgetTrendDailyCostModel dailyCost : trendCost) {
            String date = dailyCost.getDate();

            if (!monthlyCostMap.containsKey(date)) {
                WidgetTrendMonthlyCostModel monthlyCostModel = new WidgetTrendMonthlyCostModel();
                monthlyCostModel.setDate(date);
                monthlyCostModel.setMonthlyCost(new ArrayList<>());
                monthlyCostMap.put(date, monthlyCostModel);
            }
            dailyCost.setDate(null);
            monthlyCostMap.get(date).getMonthlyCost().add(dailyCost);
        }

        WidgetTrendDataModel trendDataModel = new WidgetTrendDataModel();
        trendDataModel.setTrendCost(new ArrayList<>(monthlyCostMap.values()));
        trendDataModel.setEndDate("2023-08-31"); // Set your end date

        result.setData(trendDataModel);

        return result;
    }

    public ResultModel makeTop5Chart() {
        ResultModel result = new ResultModel();
        WidgetTop5DataModel model = new WidgetTop5DataModel();
        model.setCost(widgetDAO.selectTop5Cost());
        model.setLatestSummarizedBillDate("20230821");

        result.setData(model);
        return result;
    }

    public ResultModel makeEstimatedCost(){
        ResultModel result = new ResultModel();
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("currentMonthCost", 253703.31941104392);
        resultData.put("currentMonthEstimatedCost", 278833.81);
        resultData.put("lastMonthTotalCost", 278496.6294723971);
        resultData.put("increaseDecreaseRate", 0.1210716726596246);
        resultData.put("selectedVendor", "AWS");

        result.setData(resultData);
        return result;
    }

    public ResultModel makeCostMonth(){
        ResultModel result = new ResultModel();
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("currentMonthDate", "2023-08-21");
        resultData.put("currentMonthCost", 253703.31941104392);
        resultData.put("lastMonthDate", "2023-07-21");
        resultData.put("lastMonthCost", 204854.45871085938);
        resultData.put("increaseDecreaseRate", 23.84564192919617);
        resultData.put("selectedVendor", null);

        result.setData(resultData);
        return result;
    }

    public ResultModel makeAbnormal(){
        ResultModel result = new ResultModel();
        WidgetAbnormalDataModel summaryData = widgetDAO.getAbnormalSummary();
        summaryData.setAbnormalList(widgetDAO.getAbnormalList());

        result.setData(summaryData);
        return result;
    }

    public ResultModel makeCostChart() throws ParseException {
        ResultModel result = new ResultModel();
        WidgetCostDataModel resultData = new WidgetCostDataModel();
        resultData.setTimeFrameScope(WidgetCostDataModel.WidgetCostTimeFrameModel.builder()
                .endDate(parseDate("2023-08-21"))
                .startDate(parseDate("2022-09-01")).build());
        resultData.setCustomFilters(widgetDAO.getCostCustomFilters());

        List<WidgetCostListModel> costByConditionData = widgetDAO.getCostList();
        Map<String, List<WidgetCostListModel>> groupedData = new HashMap<>();
        for (WidgetCostListModel rawData : costByConditionData) {
            String date = rawData.getDate();
            rawData.setDate(null);
            groupedData.computeIfAbsent(date, k -> new ArrayList<>()).add(rawData);
        }

        List<Map<String, Object>> costByConditionResult = new ArrayList<>();
        for (Map.Entry<String, List<WidgetCostListModel>> entry : groupedData.entrySet()) {
            String date = entry.getKey();
            List<WidgetCostListModel> costList = entry.getValue();

            Map<String, Object> groupData = new HashMap<>();
            groupData.put("date", date);
            groupData.put("cost", costList);

            costByConditionResult.add(groupData);
        }
        resultData.setCostByCondition(costByConditionResult.stream()
                .sorted(Comparator.comparing(item -> (String) item.get("date")))
                .collect(Collectors.toList()));

        result.setData(resultData);
        return result;
    }

    private static Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(dateStr);
    }

    public ResultModel makeProductPortionChart() {
        ResultModel result = new ResultModel();
        List<WidgetProductPortionAccountModel> accountModel = widgetDAO.getProductPortionAccount();
        List<WidgetProductPortionTimeFrameModel> timeFrameModel = widgetDAO.getProductPortionTimeFrame();
        List<WidgetProductPortionItemsModel> itemsModel = widgetDAO.getProductPortionItem();
        List<WidgetProductPortionPortionModel> familyModel = widgetDAO.getProductPortionFamily();
        WidgetProductPortionDataModel model = new WidgetProductPortionDataModel();

        for (WidgetProductPortionPortionModel family : familyModel) {
            List<WidgetProductPortionItemsModel> familyItems = new ArrayList<>();

            for (WidgetProductPortionItemsModel item : itemsModel) {
                if (family.getFamilyCode().equals(item.getFamilyCode())) {
                    familyItems.add(item);
                }
            }
            family.setFamilyItems(familyItems);

            if ("others".equals(family.getFamilyCode())) {
                family.setItem("others");
                family.setItemAlias("others");
                family.setFamilyCode("others");
                int othersCount = 0;
                for (WidgetProductPortionItemsModel item : family.getFamilyItems()) {
                    if (item.getIsOthers()) {
                        othersCount++;
                    }
                }
                family.setNumberOfOthers(othersCount);
            }
        }

        model.setPortion(familyModel);
        model.setAccounts(accountModel);
        model.setSelectedAccount("000885530975");
        model.setSelectedTimeFrame("202308");
        model.setTimeFrameList(timeFrameModel);
        model.setLatestSummarizedBillDate("20230821");

        result.setData(model);

        return result;
    }

}
