package com.mcmp.costbe.usage;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.usage.model.bill.*;
import com.mcmp.costbe.usage.model.filter.ProjectsModel;
import com.mcmp.costbe.usage.model.filter.WorkspacesModel;
import com.mcmp.costbe.usage.service.UsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/costopti/be")
public class UsageController {
    @Autowired
    private UsageService usageService;

    @Deprecated
    @GetMapping(path = "/getWorkspaces")
    @Tag(name = "User", description = "User API")
    @Operation(summary = "워크스페이스 목록 조회", description = "워크스페이스 목록을 조회합니다.", deprecated = true)
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity<ResultModel> getWorkspaces(){
        ResultModel result = new ResultModel();
        try{
            List<WorkspacesModel> data = usageService.getWorkspaces();
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getWorkspaces");
        }
        return ResponseEntity.ok(result);
    }

    @Deprecated
    @GetMapping(path = "/getProjects")
    @Tag(name = "User", description = "User API")
    @Operation(summary = "프로젝트 목록 조회", description = "모든 프로젝트 목록을 조회합니다.", deprecated = true)
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity<ResultModel> getProjects(){
        ResultModel result = new ResultModel();
        try{
            List<ProjectsModel> data = usageService.getProjects();
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getProjects");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/getCurMonthBill")
    @Tag(name = "Cost Dashboard", description = "Cost Dashboard overview API")
    @Operation(summary = "이번달 비용 조회", description = "지난달 대비 이번달 비용을 확인합니다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = BillingWidgetModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getCurMonthBill(@RequestBody BillingWidgetReqModel req) throws IOException {
        ResultModel result = new ResultModel();
        try{
            BillingWidgetModel data = usageService.getBillingMonthlyWidget(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getCurMonthBill");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/getTop5Bill")
    @Tag(name = "Cost Dashboard", description = "Cost Dashboard overview API")
    @Operation(summary = "이번달 상위 5개 리소스 비용 조회", description = "이번달에 사용한 비용 상위 5개의 리소스와 비용을 확인합니다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = Top5WidgetModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getTop5Bill(@RequestBody Top5WidgetReqModel req) throws IOException {
        ResultModel result = new ResultModel();
        try{
            Top5WidgetModel data = usageService.getTop5Bill(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getTop5Bill");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/getBillAsset")
    @Tag(name = "Cost Dashboard", description = "Cost Dashboard overview API")
    @Operation(summary = "이번달 사용 서비스별 비용 조회", description = "이번달 사용한 서비스(VM, DB 등) 단위의 비용을 확인합니다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
            content = {@Content(schema = @Schema(implementation = BillingAssetWidgetModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getBillAsset(@RequestBody BillingAssetReqModel req) throws IOException {
        ResultModel result = new ResultModel();
        try{
            BillingAssetWidgetModel data = usageService.getBillAsset(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getBillAsset");
        }
        return ResponseEntity.ok(result);
    }

}
