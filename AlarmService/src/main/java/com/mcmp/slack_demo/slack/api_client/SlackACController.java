package com.mcmp.slack_demo.slack.api_client;

import com.mcmp.slack_demo.slack.encryto.TokenService;
import com.mcmp.slack_demo.slack.model.SaveTokenModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackACController {

    private final SlackACService slackACService;

    private TokenService tokenService;

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

        slackACService.sendMessage(userId, message, linkUrl, linkText);
        return "Message sent to Slack successfully";
    }

    @PostMapping("/insertToken")
    public void insertToken(String id, String token, String channel) throws Exception {
        SaveTokenModel model = new SaveTokenModel();
        model.setId(id);
        model.setToken(token);
        model.setChannel(channel);
        tokenService.storeToken(model);
        System.out.println("token & channelId insert ok");
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