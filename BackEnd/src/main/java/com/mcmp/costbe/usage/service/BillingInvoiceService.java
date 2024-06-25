package com.mcmp.costbe.usage.service;

import com.mcmp.costbe.common.model.DateRangeModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.usage.dao.BillingInvoiceDao;
import com.mcmp.costbe.usage.model.bill.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.usage.model.bill.BillingInvoiceBaseInfoReqModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillingInvoiceService {

    @Autowired
    private BillingInvoiceDao billingInvoiceDao;

    @Autowired
    private DateCalculator dateCalculator;

    public List<BillingInvoiceBaseInfoModel> getCurMonthBill(BillingInvoiceBaseInfoReqModel req){
        String curMonth = dateCalculator.curMonthDate(req.getToday());
        DateRangeModel curMonthRange = dateCalculator.dateRangeCalculator(curMonth);

        req.setCurMonthStartDate(curMonthRange.getStartDate());
        req.setCurMonthEndDate(curMonthRange.getEndDate());

        BillingInvoiceBaseInfoModel aws_result = billingInvoiceDao.getCurMonthBill(req);
        aws_result.setCsp("AWS");
        aws_result.setColorClass("bg-google");

        BillingInvoiceBaseInfoModel gcp_result = new BillingInvoiceBaseInfoModel();
        gcp_result.setCsp("GCP");
        gcp_result.setCost(3985);
        gcp_result.setColorClass("bg-facebook");

        BillingInvoiceBaseInfoModel azure_result = new BillingInvoiceBaseInfoModel();
        azure_result.setCsp("AZURE");
        azure_result.setCost(3985);
        azure_result.setColorClass("bg-red");

        BillingInvoiceBaseInfoModel ncp_result = new BillingInvoiceBaseInfoModel();
        ncp_result.setCsp("NCP");
        ncp_result.setCost(3985);
        ncp_result.setColorClass("bg-green");

        List<BillingInvoiceBaseInfoModel> result = new ArrayList<>();
        result.add(aws_result);
        result.add(gcp_result);
        result.add(azure_result);
        result.add(ncp_result);

        return result;
    }
}
