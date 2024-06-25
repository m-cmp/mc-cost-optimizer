package com.mcmp.costbe.invoice.service;

import com.mcmp.costbe.common.model.DateRangeModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.invoice.dao.InvoiceDao;
import com.mcmp.costbe.invoice.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InvoiceService {

    @Autowired
    private DateCalculator dateCalculator;

    @Autowired
    private InvoiceDao invoiceDao;

    public SummaryBillItemModel getAWSSummary(SummaryReqModel req, List<LocalDateTime> calPeriodDated ){


        req.setSummaryPeriod(calPeriodDated);

        List<Double> bill = new ArrayList<>();
        for(int i = 0; i<calPeriodDated.size(); i++) {
            req.setSummaryPeriodDate(calPeriodDated.get(i));
            bill.add(invoiceDao.getSummary(req));
        }
        SummaryBillItemModel summaryAWSItem = new SummaryBillItemModel();
        summaryAWSItem.setBill(bill);
        summaryAWSItem.setCsp("AWS");


        return summaryAWSItem;
    }

    public SummaryBillItemModel getGCPSummary(){
        List<Double> bill = List.of(1.002, 0.00127, 3.0, 0.0003, 0.154, 0.012, 0.003);

        SummaryBillItemModel summaryGCPItem = new SummaryBillItemModel();
        summaryGCPItem.setBill(bill);
        summaryGCPItem.setCsp("GCP");


        return summaryGCPItem;
    }

    public SummaryBillItemModel getAzureSummary(){
        List<Double> bill = List.of(0.002, 0.0, 1.02, 0.0002, 0.152, 0.13, 0.01);

        SummaryBillItemModel summaryAzureItem = new SummaryBillItemModel();
        summaryAzureItem.setBill(bill);
        summaryAzureItem.setCsp("AZURE");


        return summaryAzureItem;
    }

    public SummaryBillItemModel getNcpSummary(){
        List<Double> bill = List.of(0.001, 0.0, 0.0, 0.012, 0.13, 0.12, 0.01);

        SummaryBillItemModel summaryNcpItem = new SummaryBillItemModel();
        summaryNcpItem.setBill(bill);
        summaryNcpItem.setCsp("NCP");


        return summaryNcpItem;
    }

    public SummaryBillItemModel getTotalSummary(List<SummaryBillItemModel> summaryBill){
        SummaryBillItemModel summaryTotalItem = new SummaryBillItemModel();
        List<Double> totalBill = new ArrayList<>();

        for(int i=0; i<summaryBill.get(0).getBill().size(); i++){
            double sum = 0.0;
            for(SummaryBillItemModel item : summaryBill){
                sum += item.getBill().get(i);
            }
            totalBill.add(sum);
        }

        summaryTotalItem.setBill(totalBill);
        summaryTotalItem.setCsp("Total");

        return summaryTotalItem;
    }

    public List<InvoiceItemModel> getAWSInvoice(InvoiceReqModel req){
        DateRangeModel dateRange = dateCalculator.dateRangeCalculator(req.getToday());
        req.setCurMonthStartDate(dateRange.getStartDate());
        req.setCurMonthEndDate(dateRange.getEndDate());

        return invoiceDao.getAWSInvoice(req);
    }
}
