package com.mcmp.costbe.usage;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.usage.model.bill.*;
import com.mcmp.costbe.usage.model.filter.ProjectsModel;
import com.mcmp.costbe.usage.model.filter.WorkspacesModel;
import com.mcmp.costbe.usage.service.UsageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v2")
public class UsageController {
    @Autowired
    private UsageService usageService;

    @GetMapping(path = "/getWorkspaces")
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

    @GetMapping(path = "/getProjects")
    public ResponseEntity<ResultModel> getProjects(@RequestParam String workspaceCD){
        ResultModel result = new ResultModel();
        try{
            List<ProjectsModel> data = usageService.getProjects(workspaceCD);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getProjects");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/getCurMonthBill")
    public ResponseEntity getCurMonthBill(@RequestBody BillingWidgetReqModel req) throws IOException {
        ResultModel result = new ResultModel();
        try{
            BillingWidgetModel data = usageService.getBillingWidget(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to getCurMonthBill");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/getTop5Bill")
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
