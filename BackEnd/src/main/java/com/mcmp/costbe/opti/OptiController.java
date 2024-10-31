package com.mcmp.costbe.opti;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoModel;
import com.mcmp.costbe.invoice.model.BillingInvoiceBaseInfoReqModel;
import com.mcmp.costbe.opti.model.*;
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
@RequestMapping(path = "/api/costopti/be/opti")
@Tag(name = "Cost Optimization", description = "Cost Optimization API")
public class OptiController {

    @Autowired
    private OptiService optiService;

    @PostMapping(path = "/unusedRcmd")
    @Operation(summary = "미사용 자원(추천) 조회", description = "최근 24시간동안 과금이 발생한 리소스에 대하여 미사용 자원을 추천한다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = UnusedQueryRstModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getUnusedRcmd(@RequestBody UnusedReqModel req){
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

    @PostMapping(path = "/abnormalRcmd")
    @Operation(summary = "이상 비용 조회", description = "최근 24시간동안 과금이 발생한 서비스들의 이상 비용 여부를 확인한다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = AbnormalRstModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getAbrnormalRcmd(@RequestBody AbnormalReqModel req){
        ResultModel result = new ResultModel();
        try {
            AbnormalRstModel data = optiService.getOptiAbrnomal(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Failed to get Abnormal Service Info");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/instOptiSizeRcmd")
    @Operation(summary = "인스턴스 사이즈 추천 조회", description = "사용중인 인스턴스의 추천 사이즈를 확인한다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = InstOptiSizeRstModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getInstOptiSizeRcmd(@RequestBody InstOptiSizeReqModel req){
        ResultModel result = new ResultModel();
        try {
            InstOptiSizeRstModel data = optiService.getInstOptiSizeRcmd(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Failed to get OptiSize Recommend Service Info");
        }
        return ResponseEntity.ok(result);
    }

}
