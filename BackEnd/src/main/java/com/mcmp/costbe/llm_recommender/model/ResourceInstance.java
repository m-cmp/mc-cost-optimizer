package com.mcmp.costbe.llm_recommender.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * One row of the recommend-tab instance picker, sourced from servicegroup_meta.
 * spec is filled in afterwards via a live Tumblebug call (vmId/mciId/nsId).
 * usd is not present in servicegroup_meta yet, so it is left null until a
 * cost source is wired in.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceInstance {
    private String instanceId;
    private String name;
    private String csp;
    private String spec;
    private Double usd;
    private String status;

    // Internal fields used to look up the spec via Tumblebug; not exposed to the client.
    @JsonIgnore
    private String vmId;
    @JsonIgnore
    private String mciId;
    @JsonIgnore
    private String nsId;
}
