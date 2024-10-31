package com.mcmp.costbe.common;

import com.mcmp.costbe.common.config.ReadyzConfig;
import com.mcmp.costbe.common.model.ReadyzModel;
import com.mcmp.costbe.common.model.ResultModel;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/costopti/be")
public class CommonController {
    @Autowired
    private ReadyzConfig readyzConfig;

    @GetMapping(path = "/readyz")
    @Tag(name = "Application", description = "Status API")
    @Operation(summary = "어플리케이션 상태 조회", description = "어플리케이션의 상태를 조회합니다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getReadyz(){
        try{
            String status = readyzConfig.getState();

            if("OK".equals(status)){
                return ResponseEntity.ok().body("Application is ready");
            }else{
                return ResponseEntity.status(503).body(status);
            }

        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Fail to get Application Status");
        }
    }

    @PostMapping(path = "/change/readyz")
    @Tag(name = "Application", description = "Status API")
    @Operation(summary = "어플리케이션 상태 변경", description = "어플리케이션의 상태를 변경합니다.")
    @Hidden
//    @ApiResponses(value={
//            @ApiResponse(responseCode = "200", description = "성공"),
//            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
//    })
    public ResponseEntity changeReadyz(@RequestBody ReadyzModel status){
        ResultModel result = new ResultModel();
        try{
            readyzConfig.setState(status);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Fail to change Application Status");
        }

        return ResponseEntity.ok(result);
    }
}
