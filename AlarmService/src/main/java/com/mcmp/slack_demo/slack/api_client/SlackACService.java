package com.mcmp.slack_demo.slack.api_client;

import com.mcmp.slack_demo.slack.encryto.TokenService;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.Map;

@Service
public class SlackACService {
    private final TokenService tokenService;

    @Autowired
    public SlackACService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void sendMessage(String userId, String message, String linkUrl, String linkText) throws SlackApiException, IOException {
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
            ChatPostMessageResponse response = slack.methods(result.get("token")).chatPostMessage(request);
            if (!response.isOk()) {
                switch (response.getError()) {
                    case "invalid_auth":
                        throw new AuthenticationException("Invalid authentication credentials for Slack.");
                    case "channel_not_found":
                        throw new Exception("The specified channel was not found.");
                    default:
                        throw new Exception("An error occurred with Slack API: " + response.getError());
                }
            }
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}