package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.model.score.ScoreRequest;
import org.springframework.stereotype.Component;

/** Builds POST /ml-rightsize request bodies. */
@Component
public class ScoreRequestBuilder {

    public ScoreRequest build(String instanceId) {
        ScoreRequest req = new ScoreRequest();
        req.setInstanceId(instanceId);
        return req;
    }
}
