package com.mcmp.costbe.usage.service;

import com.mcmp.costbe.common.model.DateRangeModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.resourceMapping.aws.AWSResourceMapping;
import com.mcmp.costbe.usage.dao.BillDao;
import com.mcmp.costbe.usage.dao.FilterDao;
import com.mcmp.costbe.usage.model.bill.*;
import com.mcmp.costbe.usage.model.filter.ProjectsModel;
import com.mcmp.costbe.usage.model.filter.WorkspacesModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UsageService {

    @Autowired
    private FilterDao filterDao;

    @Autowired
    private BillDao billDao;

    @Autowired
    private DateCalculator dateCalculator;

    public List<WorkspacesModel> getWorkspaces(){
        return filterDao.getWorkspaces();
    }

    public List<ProjectsModel> getProjects(String workspaceCD){
        return filterDao.getProjects(workspaceCD);
    }

    public BillingWidgetModel getBillingWidget(BillingWidgetReqModel req){
        String prevMonth = dateCalculator.prevMonthdate(req.getToday());
        DateRangeModel curMonthRange = dateCalculator.dateRangeCalculator(req.getToday());
        DateRangeModel prevMonthRange = dateCalculator.dateRangeCalculator(prevMonth);

        req.setCurMonthStartDate(curMonthRange.getStartDate());
        req.setCurMonthEndDate(curMonthRange.getEndDate());
        req.setPrevMonthStartDate(prevMonthRange.getStartDate());
        req.setPrevMonthEndDate(prevMonthRange.getEndDate());

        BillingWidgetModel result = billDao.getCurPrevMonthBill(req);
        result.setCurYear(req.getToday().substring(0, 4));
        result.setCurMonth(req.getToday().substring(4, 6));
        result.setPrevYear(prevMonth.substring(0, 4));
        result.setPrevMonth(prevMonth.substring(4,6));

        Double curBill = result.getCurMonthBill();
        Double prevBill = result.getPrevMonthBill();
        result.setMomPer(String.valueOf(calculatePercentageChange(prevBill, curBill)));
        result.setMomBill(calculateAbsoluteChange(prevBill, curBill));

        result.setMonthlyBill(billDao.getMonthBill(req));
        result.setSelectedCsps(req.getSelectedCsps());
        result.setSelectedProjects(req.getSelectedProjects());

        return result;
    }

    public Top5WidgetModel getTop5Bill(Top5WidgetReqModel req){
        DateRangeModel curMonthRange = dateCalculator.dateRangeCalculator(req.getToday());
        req.setCurMonthStartDate(curMonthRange.getStartDate());
        req.setCurMonthEndDate(curMonthRange.getEndDate());

        Top5WidgetModel result = new Top5WidgetModel();
        List<Top5BillModel> top5bill = billDao.getTop5Bill(req);

        for(Top5BillModel item : top5bill){
           if("others".equals(item.getResourceNm())){
               item.setIsOthers(true);
           }else{
               item.setIsOthers(false);
           }
        }

        System.out.println(top5bill.size());
        if(top5bill.isEmpty()){
            Top5BillModel temp = new Top5BillModel();
            temp.setBill(0.0);
            temp.setCsp("Null");
            temp.setIsOthers(false);
            temp.setResourceNm("Null");

            top5bill.add(temp);
        }

        result.setTop5bill(top5bill);
        result.setSelectedProjects(req.getSelectedProjects());
        result.setSelectedCsps(req.getSelectedCsps());
        result.setCurYear(req.getToday().substring(0, 4));
        result.setCurMonth(req.getToday().substring(4,6));

        return result;
    }

    public BillingAssetWidgetModel getBillAsset(BillingAssetReqModel req){
        BillingAssetWidgetModel result = new BillingAssetWidgetModel();

        DateRangeModel curMonthRange = dateCalculator.dateRangeCalculator(req.getToday());
        req.setCurMonthStartDate(curMonthRange.getStartDate());
        req.setCurMonthEndDate(curMonthRange.getEndDate());

        List<String> familyCode = List.of("Virtual Machine", "Storage", "Database", "LB");
        List<BillingAssetModel> billingAsset = new ArrayList<>();

        for(String item : familyCode){
            BillingAssetModel familyItem = new BillingAssetModel();
            List<String> childProducts = AWSResourceMapping.getData(item);
            req.setAWSChildProducts(childProducts);

            List<BillingAssetChildModel> childItem = billDao.getBillAssetChild(req);
            double childsTotalBill = 0.0;
            Integer childsTotalUnit = 0;
            for(BillingAssetChildModel child : childItem){
                childsTotalUnit += child.getUnit();
                childsTotalBill += child.getBill();
            }

            familyItem.setChildProductCode(childItem);
            familyItem.setTotalCost(childsTotalBill);
            familyItem.setTotalUnit(childsTotalUnit);
            familyItem.setFamilyProductCode(item);

            billingAsset.add(familyItem);
        }

        result.setSelectedProjects(req.getSelectedProjects());
        result.setSelectedCsps(req.getSelectedCsps());
        result.setCurYear(req.getToday().substring(0, 4));
        result.setCurMonth(req.getToday().substring(4,6));
        result.setBillingAsset(billingAsset);

        return result;
    }

    public double calculatePercentageChange(double prevMonthBill, double curMonthBill) {
        if (prevMonthBill == 0) {
            return curMonthBill == 0 ? 0 : (curMonthBill > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
        }
        return ((curMonthBill - prevMonthBill) / Math.abs(prevMonthBill)) * 100;
    }

    public double calculateAbsoluteChange(double prevMonthBill, double curMonthBill) {
        return curMonthBill - prevMonthBill;
    }
}
