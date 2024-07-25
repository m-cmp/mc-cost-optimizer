package com.mcmp.costselector.unused;

import com.mcmp.costselector.unused.model.UnusedSelectReqModel;
import com.mcmp.costselector.model.common.CommonResultModel;
import com.mcmp.costselector.unused.service.UnusedSelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/batunused")
public class UnusedSelectController {
    @Autowired
    private UnusedSelectService unusedSelectService;

    @PostMapping("/selectRsp")
    public ResponseEntity<CommonResultModel> selectRsPlan(@RequestBody UnusedSelectReqModel req){
        CommonResultModel result = new CommonResultModel();
        try{
            unusedSelectService.unusedResourceSelector(req);
        } catch (Exception e){
            result.setError(400, "Selection Unused Plan - Fail");
        }
        return ResponseEntity.ok(result);
    }
}
