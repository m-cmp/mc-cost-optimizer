/**
 * ResultModel.java
 *
 * @author   soojungkim
 * @version  1.0, Jul 1, 2019
 * @description All Result Models must inherit this class.
 *
 * copyright BESPIN GLOBAL
 */

package com.mcmp.dummybe.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultModel {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_FAIL = "fail";

    @JsonProperty("status")
    private String status = STATUS_OK;

    @JsonProperty(value = "error", required = false)
    private ErrorModel error = null;

    @JsonProperty(value = "Data", required = false)
    private Object data = null;

    @JsonProperty(value = "result", required = false)
    private Object result = null;

    public void setError(String code, String message) {
        if(error == null) {
            error = new ErrorModel();
        }

        status = STATUS_FAIL;
        error.setCode(code);
        error.setMessage(message);
    }

    public ErrorModel getError() {
        return error;
    }

}

