package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.model.score.ScoreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/** Calls the ml-rightsize service (POST /ml-rightsize) for one instance's score JSON. */
@Component
@Primary
public class MlRightsizeScoreProvider implements ScoreProvider {

    @Value("${ml.rightsize.base-url}")
    private String baseUrl;

    @Autowired
    @Qualifier("llmRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public String get(ScoreRequest req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of("csp_instanceid", req.getInstanceId());

        return restTemplate.postForObject(
                baseUrl + "/ml-rightsize", new HttpEntity<>(body, headers), String.class);
    }
}
