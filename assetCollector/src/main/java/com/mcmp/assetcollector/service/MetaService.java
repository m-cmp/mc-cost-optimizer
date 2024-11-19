package com.mcmp.assetcollector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MetaService {
    @Value("${costopti.be.url}")
    private String costoptiBEUrl;

    public void updateSvcGrpMeta() {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = String.format("%s/api/costopti/be/updateRscMeta", costoptiBEUrl);

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, Void.class);
    }
}
