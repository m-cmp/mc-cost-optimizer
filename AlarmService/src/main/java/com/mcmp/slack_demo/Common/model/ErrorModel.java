package com.mcmp.slack_demo.Common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class ErrorModel {
    @JsonProperty("Code")
    private int code = 200;

    @JsonProperty("Message")
    private String message = "";
}
