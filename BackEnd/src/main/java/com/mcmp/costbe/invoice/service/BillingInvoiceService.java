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
            BillingInvoiceBaseInfoModel aws_result = billingInvoiceDao.getCurMonthBill(req);
            aws_result.setCsp("AWS");
            aws_result.setColorClass("bg-google");

            BillingInvoiceBaseInfoModel gcp_result = new BillingInvoiceBaseInfoModel();
            gcp_result.setCsp("GCP");
            gcp_result.setCost(0);
            gcp_result.setColorClass("bg-facebook");

            BillingInvoiceBaseInfoModel azure_result = new BillingInvoiceBaseInfoModel();
            azure_result.setCsp("AZURE");
            azure_result.setCost(0);
            azure_result.setColorClass("bg-red");

            BillingInvoiceBaseInfoModel ncp_result = new BillingInvoiceBaseInfoModel();
            ncp_result.setCsp("NCP");
            ncp_result.setCost(0);
            ncp_result.setColorClass("bg-green");


            result.add(aws_result);
            result.add(gcp_result);
            result.add(azure_result);
            result.add(ncp_result);

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
}
