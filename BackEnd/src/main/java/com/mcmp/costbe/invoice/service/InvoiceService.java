package com.mcmp.costbe.invoice.service;

import com.mcmp.costbe.common.model.DateRangeModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.common.service.ExceptionService;
import com.mcmp.costbe.invoice.dao.InvoiceDao;
import com.mcmp.costbe.invoice.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InvoiceService {

    @Autowired
    private DateCalculator dateCalculator;

    @Autowired
    private InvoiceDao invoiceDao;

    @Autowired
    private ExceptionService exceptionService;

    public List<SummaryBillItemsModel> getInvoiceSummaryBill(SummaryReqModel req){
        List<SummaryBillItemModel> summary = invoiceDao.getSummaryBill(req);
        Map<String, List<SummaryBillItemModel>> groupedByCsp = summary.stream()
                .collect(Collectors.groupingBy(SummaryBillItemModel::getCsp));

        List<SummaryBillItemsModel> result = groupedByCsp.entrySet().stream()
                .map(item -> new SummaryBillItemsModel(item.getKey(), item.getValue()))
                .collect(Collectors.toList());

        return result;
    }

    public List<InvoiceItemModel> getAWSInvoice(InvoiceReqModel req){
        DateRangeModel dateRange = dateCalculator.dateRangeCalculator(req.getToday());
        req.setCurMonthStartDate(dateRange.getStartDate());
        req.setCurMonthEndDate(dateRange.getEndDate());

        req.setYear_month(req.getToday().substring(0, 6));

        List<InvoiceItemModel> result = new ArrayList<>();
        boolean hasTableNotFoundError = false;

        try {
            List<InvoiceItemModel> awsData = invoiceDao.getAWSInvoice(req);
            if(awsData != null) {
                result.addAll(awsData);
            }
        } catch (BadSqlGrammarException ex){
            if(exceptionService.isTableNotFound(ex)){
                log.warn("[Invoice Widget Log] AWS NotFoundTable : {}", ex.getMessage());
                hasTableNotFoundError = true;
            } else {
                ex.printStackTrace();
                throw new RuntimeException();
            }
        }

        try {
            List<InvoiceItemModel> ncpData = invoiceDao.getNCPInvoice(req);
            if(ncpData != null) {
                result.addAll(ncpData);
            }
        } catch (BadSqlGrammarException ex){
            if(exceptionService.isTableNotFound(ex)){
                log.warn("[Invoice Widget Log] NCP NotFoundTable : {}", ex.getMessage());
            } else {
                log.warn("[Invoice Widget Log] NCP Error : {}", ex.getMessage());
            }
        }

        try {
            List<InvoiceItemModel> azureData = invoiceDao.getAzureInvoice(req);
            if(azureData != null) {
                result.addAll(azureData);
            }
        } catch (Exception ex){
            log.error("[Invoice Widget Log] Azure Error : {}", ex.getMessage(), ex);
        }

        // 테이블 없음 또는 데이터 없음 -> null 반환
        if(hasTableNotFoundError || result.isEmpty()) {
            return null;
        }

        return result;
    }
}
