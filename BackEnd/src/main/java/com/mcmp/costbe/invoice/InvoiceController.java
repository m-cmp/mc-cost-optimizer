package com.mcmp.costbe.invoice;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.invoice.model.*;
import com.mcmp.costbe.invoice.service.InvoiceService;
import com.mcmp.costbe.usage.model.filter.WorkspacesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v2/invoice")
public class InvoiceController {

    @Autowired
    private DateCalculator dateCalculator;
    @Autowired
    private InvoiceService invoiceService;

    @PostMapping(path = "/getSummary")
    public ResponseEntity<ResultModel> getSummary(@RequestBody SummaryReqModel req){
        ResultModel result = new ResultModel();
        try{
            List<LocalDateTime> calPeriodDated = dateCalculator.calculatePeriodDates(req.getToday(), req.getSelectedPeriod());

            SummaryResModel resultData = new SummaryResModel();
            resultData.setCurYear(req.getToday().substring(0, 4));
            resultData.setCurMonth(req.getToday().substring(4, 6));
            resultData.setCurDay(req.getToday().substring(6, 8));
            resultData.setDates(dateCalculator.LocalDateTimeToString(calPeriodDated));
            resultData.setSelectedCsps(req.getSelectedCsps());
            resultData.setSelectedProjects(req.getSelectedProjects());
            resultData.setSelectedPeriod(req.getSelectedPeriod());

            List<SummaryBillItemModel> summaryBillResult = new ArrayList<>();
            summaryBillResult.add(invoiceService.getAWSSummary(req, calPeriodDated));
            summaryBillResult.add(invoiceService.getGCPSummary());
            summaryBillResult.add(invoiceService.getAzureSummary());
            summaryBillResult.add(invoiceService.getNcpSummary());
            SummaryBillItemModel totalSummary = invoiceService.getTotalSummary(summaryBillResult);
            summaryBillResult.add(totalSummary);
            
            resultData.setSummaryBill(summaryBillResult);
            result.setData(resultData);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getSummary");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(path="/getInvoice")
    public ResponseEntity<ResultModel> getInvoice(@RequestBody InvoiceReqModel req){
        ResultModel result = new ResultModel();
        try{
            InvoiceResModel resultData = new InvoiceResModel();
            resultData.setCurYear(req.getToday().substring(0, 4));
            resultData.setCurMonth(req.getToday().substring(4, 6));
            resultData.setSelectedCsps(req.getSelectedCsps());
            resultData.setSelectedProjects(req.getSelectedProjects());
            resultData.setInvoice(invoiceService.getAWSInvoice(req));

            result.setData(resultData);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getSummary");
        }
        return ResponseEntity.ok(result);
    }
}
