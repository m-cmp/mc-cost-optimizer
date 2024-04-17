package com.mcmp.slack_demo.slack.api_client;

import com.mcmp.slack_demo.slack.encryto.TokenService;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class SlackACService {
    private TokenService tokenService;

    @Autowired
    public SlackACService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void sendMessage(String userId, String message, String linkUrl, String linkText) {
        Slack slack = Slack.getInstance();
        String fullMessage = message;

        if (linkUrl != null && !linkUrl.isEmpty() && linkText != null && !linkText.isEmpty()) {
            fullMessage += " <" + linkUrl + "|" + linkText + ">";
        }

        try {
            Map<String, String> result = tokenService.retrieveToken(userId);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(result.get("channel"))
                    .text(fullMessage)
                    .build();


            slack.methods(result.get("token")).chatPostMessage(request);
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}