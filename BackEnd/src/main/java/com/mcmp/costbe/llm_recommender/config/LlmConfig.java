package com.mcmp.costbe.llm_recommender.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class LlmConfig {

    @Bean(name = "llmRestTemplate")
    public RestTemplate llmRestTemplate(
            @Value("${llm.http.connect-timeout-ms:5000}") int connectTimeout,
            @Value("${llm.http.read-timeout-ms:25000}") int readTimeout) {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(connectTimeout);
        f.setReadTimeout(readTimeout);
        return new RestTemplate(f);
    }
}
