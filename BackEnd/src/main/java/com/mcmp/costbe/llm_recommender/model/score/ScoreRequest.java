package com.mcmp.costbe.llm_recommender.model.score;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** POST /score request body, per LLM_DEV_SPEC.html section 4. */
@Getter
@Setter
public class ScoreRequest {

    @JsonProperty("instance_id")
    private String instanceId;

    @JsonProperty("interval_seconds")
    private Integer intervalSeconds;

    private List<ScoreSample> samples;
}
