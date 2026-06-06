package com.mcmp.costbe.llm_recommender.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Spec §7 output fields (instance, recommendation, detail, reasoning, confidence)
 * plus an internal render wrapper (status, error). status is NOT a spec field.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recommendation {

    public static final String STATUS_OK = "ok";
    public static final String STATUS_INSUFFICIENT = "insufficient_data";
    public static final String STATUS_ERROR = "error";

    private String instance;
    private String recommendation; // terminate|downsize|upsize|migrate|keep
    private String detail;
    private String reasoning;
    private String confidence;     // high|medium|low

    private String status;         // ok|insufficient_data|error (internal)
    private String error;          // human-safe message when status=error (never contains keys)

    public static Recommendation insufficient(String instance) {
        Recommendation r = new Recommendation();
        r.instance = instance;
        r.status = STATUS_INSUFFICIENT;
        return r;
    }

    public static Recommendation error(String instance, String message) {
        Recommendation r = new Recommendation();
        r.instance = instance;
        r.status = STATUS_ERROR;
        r.error = message;
        return r;
    }
}
