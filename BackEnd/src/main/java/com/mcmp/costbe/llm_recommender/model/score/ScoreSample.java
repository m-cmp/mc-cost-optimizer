package com.mcmp.costbe.llm_recommender.model.score;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** One CPU/MEM usage sample, per LLM_DEV_SPEC.html section 4 POST /score request. */
@Getter
@Setter
public class ScoreSample {

    private String timestamp;

    @JsonProperty("CPUUsage")
    private Double cpuUsage;

    @JsonProperty("MEMUsage")
    private Double memUsage;
}
