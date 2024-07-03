package com.mcmp.costbe.invoice;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.common.service.DateCalculator;
import com.mcmp.costbe.invoice.model.*;
import com.mcmp.costbe.invoice.service.InvoiceService;
import com.mcmp.costbe.usage.model.bill.BillingWidgetModel;
import com.mcmp.costbe.usage.model.filter.WorkspacesModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v2/invoice")
@Tag(name = "Billing Invoice", description = "Billing Invoice API")
public class InvoiceController {

    @Autowired
    private DateCalculator dateCalculator;
    @Autowired
    private InvoiceService invoiceService;

    @PostMapping(path = "/getSummary")
    @Operation(summary = "빌링 인보이스 날짜별 조회", description = "CSP별 빌링 인보이스 비용을 날짜별로 확인한다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = SummaryResModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
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
    @Operation(summary = "이번달 빌링 인보이스 조회", description = "이번달 빌링 인보이스 내역을 확인한다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = InvoiceResModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
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
