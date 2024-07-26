package com.mcmp.costselector.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorModel {
    /** The Error Code. */
    @JsonProperty("code")
    private int code = 200;

    /** The Error Message. */
    @JsonProperty("message")
    private String message = "";
}
