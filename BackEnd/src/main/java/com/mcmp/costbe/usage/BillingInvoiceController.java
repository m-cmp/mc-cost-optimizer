package com.mcmp.costbe.usage;

import com.mcmp.costbe.invoice.model.InvoiceResModel;
import com.mcmp.costbe.usage.model.bill.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.usage.model.bill.BillingInvoiceBaseInfoReqModel;
import com.mcmp.costbe.usage.service.BillingInvoiceService;
import com.mcmp.costbe.common.model.ResultModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v2/invoice")
@Tag(name = "Billing Invoice", description = "Billing Invoice API")
public class BillingInvoiceController {
    @Autowired
    private BillingInvoiceService billingInvoiceService;

    @PostMapping(path = "/getBillingBaseInfo")
    @Operation(summary = "빌링 인보이스 요약 조회", description = "이번달 CSP별 요약된 빌링 인보이스를 확인한다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = BillingInvoiceBaseInfoModel.class)))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getBillingBaseInfo(@RequestBody BillingInvoiceBaseInfoReqModel req) throws IOException {
        ResultModel result = new ResultModel();
        try {
            List<BillingInvoiceBaseInfoModel> data = billingInvoiceService.getCurMonthBill(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Failed to get Billing Base Info");
        }
        return ResponseEntity.ok(result);
    }
}
