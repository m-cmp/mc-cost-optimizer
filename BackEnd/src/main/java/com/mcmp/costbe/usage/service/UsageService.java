package com.mcmp.costbe.usage.service;

import com.mcmp.costbe.common.model.DateRangeModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.common.service.ExceptionService;
import com.mcmp.costbe.resourceMapping.MultiCSPResourceMapping;
import com.mcmp.costbe.usage.dao.BillDao;
import com.mcmp.costbe.usage.dao.FilterDao;
import com.mcmp.costbe.usage.model.bill.*;
import com.mcmp.costbe.usage.model.filter.ProjectsModel;
import com.mcmp.costbe.usage.model.filter.WorkspacesModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
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
    private ExceptionService exceptionService;

    @Autowired
    private DateCalculator dateCalculator;

    public List<WorkspacesModel> getWorkspaces(){
        return filterDao.getWorkspaces();
    }

    public List<ProjectsModel> getProjects(){
        return filterDao.getProjects();
    }

    public BillingWidgetModel getBillingMonthlyWidget(BillingWidgetReqModel req){
        String prevMonth = dateCalculator.prevMonthdate(req.getToday());
        req.setCurYearMonth(req.getToday().substring(0, 6));
        req.setPrevYearMonth(prevMonth.substring(0, 6));
        req.setPrevMonths(dateCalculator.getLast12Months(req.getToday()));

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

    public Top5WidgetModel getTop5Bill(Top5WidgetReqModel req) {
        DateRangeModel curMonthRange = dateCalculator.dateRangeCalculator(req.getToday());
        req.setCurMonthStartDate(curMonthRange.getStartDate());
        req.setCurMonthEndDate(curMonthRange.getEndDate());
        req.setYear_month(req.getToday().substring(0,6));

        Top5WidgetModel result = new Top5WidgetModel();
        try{
            List<Top5BillModel> top5bill = billDao.getTop5Bill(req);

            for(Top5BillModel item : top5bill){
                if("others".equals(item.getResourceNm())){
                    item.setIsOthers(true);
                }else{
                    item.setIsOthers(false);
                }
            }

            if(top5bill.isEmpty()){
                // 데이터 없음 -> null 반환
                result.setTop5bill(null);
            } else {
                result.setTop5bill(top5bill);
            }
        } catch (BadSqlGrammarException ex){
            if(exceptionService.isTableNotFound(ex)){
                log.warn("[Top5 Widget Log]NotFoundTable : {}", ex.getMessage());
                // 테이블 없음 -> null 반환
                result.setTop5bill(null);
            }else {
                ex.printStackTrace();
                throw new RuntimeException();
            }
        }

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
        req.setYear_month(req.getToday().substring(0, 6));

        List<String> familyCode = MultiCSPResourceMapping.getAllCategories();
        List<BillingAssetModel> billingAsset = new ArrayList<>();

        try{
            for(String category : familyCode){
                BillingAssetModel familyItem = new BillingAssetModel();

                // 모든 CSP의 해당 카테고리 서비스들을 수집
                List<String> allChildProducts = new ArrayList<>();
                if(req.getSelectedCsps() != null && !req.getSelectedCsps().isEmpty()) {
                    for(String csp : req.getSelectedCsps()) {
                        List<String> cspServices = MultiCSPResourceMapping.getServicesByCategory(csp, category);
                        allChildProducts.addAll(cspServices);
                    }
                } else {
                    // CSP 선택이 없으면 모든 CSP 포함
                    for(String csp : List.of("AWS", "NCP", "AZURE")) {
                        List<String> cspServices = MultiCSPResourceMapping.getServicesByCategory(csp, category);
                        allChildProducts.addAll(cspServices);
                    }
                }


                req.setMultiCSPChildProducts(allChildProducts);

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
                familyItem.setFamilyProductCode(category);

                billingAsset.add(familyItem);
            }

            // 데이터 없음 -> null 반환
            if(billingAsset.isEmpty()){
                result.setBillingAsset(null);
            } else {
                result.setBillingAsset(billingAsset);
            }
        } catch (BadSqlGrammarException ex){
            if(exceptionService.isTableNotFound(ex)){
                log.warn("[BillAsset Widget Log]NotFoundTable : {}", ex.getMessage());
                // 테이블 없음 -> null 반환
                result.setBillingAsset(null);
            } else {
                ex.printStackTrace();
                throw new RuntimeException();
            }
        }

        result.setSelectedProjects(req.getSelectedProjects());
        result.setSelectedCsps(req.getSelectedCsps());
        result.setCurYear(req.getToday().substring(0, 4));
        result.setCurMonth(req.getToday().substring(4,6));

        return result;
    }

    public double calculatePercentageChange(double prevMonthBill, double curMonthBill) {
        if (prevMonthBill == 0) {
            return curMonthBill == 0 ? 0 : (curMonthBill > 0 ? 100 : -100);
        }
        return ((curMonthBill - prevMonthBill) / Math.abs(prevMonthBill)) * 100;
    }

    public double calculateAbsoluteChange(double prevMonthBill, double curMonthBill) {
        return curMonthBill - prevMonthBill;
    }
}
