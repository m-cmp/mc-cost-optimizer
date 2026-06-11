package com.mcmp.costbe.llm_recommender.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * One row of the recommend-tab instance picker, sourced from servicegroup_meta.
 * spec/usd are not present in servicegroup_meta yet, so they are left null
 * until a cost/spec source is wired in.
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
}
