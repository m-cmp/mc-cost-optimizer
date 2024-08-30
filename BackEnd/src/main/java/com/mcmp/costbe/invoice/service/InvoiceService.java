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

import java.time.LocalDateTime;
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

        try {
            return invoiceDao.getAWSInvoice(req);
        } catch (BadSqlGrammarException ex){
            if(exceptionService.isTableNotFound(ex)){
                log.warn("[Invoice Widget Log] NotFoundTable : {}", ex.getMessage());
            } else {
                ex.printStackTrace();
                throw new RuntimeException();
            }
        }

        return null;
    }
}
