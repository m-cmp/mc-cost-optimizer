package com.mcmp.slack_demo.slack.api_client;

import com.mcmp.slack_demo.slack.encryto.TokenService;
import com.mcmp.slack_demo.slack.model.SaveTokenModel;
import com.slack.api.methods.SlackApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class SlackACController {

    private final SlackACService slackACService;

    private final TokenService tokenService;

    @Autowired
    public SlackACController(SlackACService slackACService, TokenService tokenService) {
        this.slackACService = slackACService;
        this.tokenService = tokenService;
    }

    @PostMapping("/sendAC")
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

    @PostMapping("/insertToken")
    public String insertToken(@RequestBody SaveTokenModel model) throws Exception {
        tokenService.storeToken(model);
        return "token & channelId insert ok";
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