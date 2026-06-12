package com.mcmp.costbe.llm_recommender.model.score;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** POST /ml-rightsize request body. */
@Getter
@Setter
public class ScoreRequest {

    @JsonProperty("instance_id")
    private String instanceId;
}
