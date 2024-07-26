package com.mcmp.costbe.opti;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoReqModel;
import com.mcmp.costbe.opti.model.UnusedQueryParamModel;
import com.mcmp.costbe.opti.model.UnusedQueryRstModel;
import com.mcmp.costbe.opti.model.UnusedReqModel;
import com.mcmp.costbe.opti.model.UnusedRstModel;
import com.mcmp.costbe.opti.service.OptiService;
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
@RequestMapping(path = "/api/v2/opti")
@Tag(name = "Cost Optimization", description = "Cost Optimization API")
public class OptiController {

    @Autowired
    private OptiService optiService;

    @PostMapping(path = "/unusedRec")
    @Operation(summary = "미사용 자원(추천) 조회", description = "이번달 CSP별 요약된 빌링 인보이스를 확인한다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = UnusedQueryRstModel.class)))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getUnusedRec(@RequestBody UnusedReqModel req){
        ResultModel result = new ResultModel();
        try {
            UnusedRstModel data = optiService.getOptiUnused(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Failed to get Unused Resource Info");
        }
        return ResponseEntity.ok(result);
    }

}
