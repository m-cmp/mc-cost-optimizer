package com.mcmp.costbe.alarm;

import com.mcmp.costbe.alarm.model.AlarmHistoryReqModel;
import com.mcmp.costbe.alarm.model.AlarmHistoryRstModel;
import com.mcmp.costbe.alarm.service.AlarmHistoryService;
import com.mcmp.costbe.common.model.ResultModel;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping(path = "/api/costopti/be/alarm")
@Tag(name = "Cost Alarm", description = "Cost Alarm API")
public class AlarmController {

    @Autowired
    private AlarmHistoryService alarmHistoryService;

    @PostMapping(path = "/history")
    @Operation(summary = "알람 발생 내역 조회", description = "최근 7일간 발생한 최적화 알람을 조회한다.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "성공",
                    content = {@Content(schema = @Schema(implementation = AlarmHistoryRstModel.class))}),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = {@Content(examples = {})})
    })
    public ResponseEntity getAlarmHistory(@RequestBody AlarmHistoryReqModel req){
        ResultModel result = new ResultModel();
        try {
            AlarmHistoryRstModel data = alarmHistoryService.getAlarmHistory(req);
            result.setData(data);
        } catch (Exception e){
            e.printStackTrace();
            result.setError(500, "Failed to get Alarm History Info");
        }
        return ResponseEntity.ok(result);
    }
}
