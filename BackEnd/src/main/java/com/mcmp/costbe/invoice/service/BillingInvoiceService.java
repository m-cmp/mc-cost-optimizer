package com.mcmp.costbe.invoice.service;

import com.mcmp.costbe.common.model.DateRangeModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.common.service.ExceptionService;
import com.mcmp.costbe.invoice.dao.BillingInvoiceDao;
import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoReqModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BillingInvoiceService {

    @Autowired
    private BillingInvoiceDao billingInvoiceDao;

    @Autowired
    private DateCalculator dateCalculator;

    @Autowired
    private ExceptionService exceptionService;

    public List<BillingInvoiceBaseInfoModel> getBaseInfo(BillingInvoiceBaseInfoReqModel req){
        String curMonth = dateCalculator.curMonthDate(req.getToday());
        DateRangeModel curMonthRange = dateCalculator.dateRangeCalculator(curMonth);

        req.setCurMonthStartDate(curMonthRange.getStartDate());
        req.setCurMonthEndDate(curMonthRange.getEndDate());
        req.setYear_month(req.getToday().substring(0, 6));

        List<BillingInvoiceBaseInfoModel> result = new ArrayList<>();
        try {
            List<BillingInvoiceBaseInfoModel> queryResults = billingInvoiceDao.getCurMonthBill(req);

            if (queryResults != null) {
                for (BillingInvoiceBaseInfoModel item : queryResults) {
                    item.setColorClass(getColorClass(item.getCsp()));
                    result.add(item);
                }
            }

            // 결과가 없으면 모든 CSP를 0원으로 추가
            if (result.isEmpty()) {
                addDefaultCsps(result);
            } else {
                // 누락된 CSP들을 0원으로 추가
                addMissingCsps(result);
            }

        } catch (BadSqlGrammarException ex){
            if(exceptionService.isTableNotFound(ex)){
                log.warn("[BaseInfo Widget Log] NotFoundTable : {}", ex.getMessage());
            } else {
                ex.printStackTrace();
                throw new RuntimeException();
            }
        }
        return result;
    }

    private String getColorClass(String csp) {
        switch(csp) {
            case "AWS": return "bg-google";
            case "NCP": return "bg-green";
            case "AZURE": return "bg-red";
            case "GCP": return "bg-facebook";
            default: return "bg-gray";
        }
    }

    private void addDefaultCsps(List<BillingInvoiceBaseInfoModel> result) {
        String[] csps = {"AWS", "GCP", "AZURE", "NCP"};
        for (String csp : csps) {
            BillingInvoiceBaseInfoModel item = new BillingInvoiceBaseInfoModel();
            item.setCsp(csp);
            item.setCost(0);
            item.setColorClass(getColorClass(csp));
            result.add(item);
        }
    }

    private void addMissingCsps(List<BillingInvoiceBaseInfoModel> result) {
        String[] allCsps = {"AWS", "GCP", "AZURE", "NCP"};
        List<String> existingCsps = result.stream()
                .map(BillingInvoiceBaseInfoModel::getCsp)
                .collect(java.util.stream.Collectors.toList());

        for (String csp : allCsps) {
            if (!existingCsps.contains(csp)) {
                BillingInvoiceBaseInfoModel item = new BillingInvoiceBaseInfoModel();
                item.setCsp(csp);
                item.setCost(0);
                item.setColorClass(getColorClass(csp));
                result.add(item);
            }
        }
    }
}
