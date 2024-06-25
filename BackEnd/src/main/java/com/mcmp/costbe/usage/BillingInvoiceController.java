package com.mcmp.costbe.usage;

import com.mcmp.costbe.usage.model.bill.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.usage.model.bill.BillingInvoiceBaseInfoReqModel;
import com.mcmp.costbe.usage.service.BillingInvoiceService;
import com.mcmp.costbe.common.model.ResultModel;
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
public class BillingInvoiceController {
    @Autowired
    private BillingInvoiceService billingInvoiceService;

    @PostMapping(path = "/getBillingBaseInfo")
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
