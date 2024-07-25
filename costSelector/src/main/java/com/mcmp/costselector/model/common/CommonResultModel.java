package com.mcmp.costselector.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResultModel {
    public static final String STATUS_OK = "OK";
    public static final String STATUS_FAIL = "fail";

    @JsonProperty("status")
    private String status = STATUS_OK;

    @JsonProperty(value = "Data", required = false)
    private Object data = null;

    @JsonProperty(value = "error")
    private ErrorModel error = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setError(ResponseSpecification code) {
        if(error == null) {
            error = new ErrorModel();
        }

        status = STATUS_FAIL;
        error.setCode(code.getCode());
        error.setMessage(code.getMessage());
    }

    public void setError(int code, String message) {
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

