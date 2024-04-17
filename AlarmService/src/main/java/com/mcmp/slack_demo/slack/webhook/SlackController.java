package com.mcmp.slack_demo.slack.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackController {

    private final SlackService slackService;

    @Autowired
    public SlackController(SlackService slackService) {
        this.slackService = slackService;
    }

    @PostMapping("/send")
    public String sendMessageToSlack(
            @RequestParam String webhookUrl,
            @RequestParam String message,
            @RequestParam(required = false) String linkUrl,
            @RequestParam(required = false) String linkText) {

        slackService.sendSlackNotification(webhookUrl, message, linkUrl, linkText);
        return "Message sent to Slack successfully";
    }
}