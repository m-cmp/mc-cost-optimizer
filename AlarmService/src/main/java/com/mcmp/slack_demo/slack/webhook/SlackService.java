package com.mcmp.slack_demo.slack.webhook;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class SlackService {

    private final RestTemplate restTemplate;

    public SlackService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendSlackNotification(String webhookUrl, String message, String linkUrl, String linkText) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonPayload;
        if (linkUrl != null && !linkUrl.isEmpty() && linkText != null && !linkText.isEmpty()) {
            jsonPayload = String.format("{\"text\": \"%s <%s|%s>\"}", message, linkUrl, linkText);
        } else {
            jsonPayload = String.format("{\"text\": \"%s\"}", message);
        }

        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
        restTemplate.postForObject(webhookUrl, request, String.class);
    }
}