package com.mcmp.slack_demo.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorModel {
    @JsonProperty("Code")
    private int code = 200;

    @JsonProperty("Message")
    private String message = "";
}
