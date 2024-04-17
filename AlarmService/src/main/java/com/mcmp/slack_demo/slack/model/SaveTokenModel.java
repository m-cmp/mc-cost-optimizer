package com.mcmp.slack_demo.slack.model;

import lombok.Data;

@Data
public class SaveTokenModel {
    private String id;
    private String token;
    private String channel;
}
