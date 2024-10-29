package com.mcmp.slack_demo.slack.api_client;

import com.mcmp.slack_demo.common.model.CommonResultModel;
import com.mcmp.slack_demo.slack.encryto.TokenService;
import com.mcmp.slack_demo.slack.model.SaveTokenModel;
import com.slack.api.methods.SlackApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/costopti/alert")
public class SlackACController {

    private final SlackACService slackACService;

    private final TokenService tokenService;

    @Autowired
    public SlackACController(SlackACService slackACService, TokenService tokenService) {
        this.slackACService = slackACService;
        this.tokenService = tokenService;
    }

    @PostMapping("/sendSlackAC")
    public String sendMessageToSlack(
            @RequestParam String userId,
            @RequestParam String message,
            @RequestParam(required = false) String linkUrl,
            @RequestParam(required = false) String linkText) {
        try {
            slackACService.sendMessage(userId, message, linkUrl, linkText);
            return "메세지 전송 성공.";
        } catch (RuntimeException | SlackApiException | IOException e) {
            if (e.getMessage().contains("Invalid authentication credentials for Slack.")) {
                return "Slack Api 토큰 인증에 실패했습니다.";
            } else if (e.getMessage().contains("The specified channel was not found.")) {
                return "해당하는 채널을 찾을 수 없습니다.";
            } else if (e.getMessage().contains("not_in_channel")) {
                return "해당하는 채널에 Chat Bot App 이 존재하지 않습니다.";
            } else {
                return "Message 전송 실패.";
            }
        }
    }

    @PostMapping("/insertSlackToken")
    public String insertToken(@RequestBody SaveTokenModel model) throws Exception {
        tokenService.storeToken(model);
        return "token & channelId insert ok";
    }

    @GetMapping("/getSlackIF")
    public ResponseEntity<?> getSlackToken(@RequestParam String userId){
        CommonResultModel result = new CommonResultModel();
        try{
            Map<String, String> data = slackACService.getSlackToken(userId);
            result.setData(data);
        }catch (Exception e){
            e.printStackTrace();
            result.setError(400, "Error Get SlackINFO");
        }
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/test")
    public void test(String id, String token, String channel) throws Exception {
        SaveTokenModel model = new SaveTokenModel();
        model.setId(id);
        model.setToken(token);
        model.setChannel(channel);
        tokenService.storeToken(model);

    }
}
