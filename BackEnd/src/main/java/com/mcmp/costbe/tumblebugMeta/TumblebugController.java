package com.mcmp.costbe.tumblebugMeta;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.tumblebugMeta.service.VMMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/api/v2")
public class TumblebugController {

    @Autowired
    private VMMetaService vmMetaService;

    @GetMapping(path = "/updateRscMeta")
    public ResponseEntity<ResultModel> updateTBBRscMeta(){
        ResultModel result = new ResultModel();
        try{
            vmMetaService.getTBBResourceMetaInfo();
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to Update (Tumblebug) ResourceMeta");
        }
        return ResponseEntity.ok(result);
    }
}
