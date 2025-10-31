package com.mcmp.cost.ncp.collector.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@Configuration
public class RestTemplateConfig {

    @Bean(name = "ncpRestTemplate")
    public RestTemplate ncpRestTemplate() {
        // 1. 커넥션 풀 설정
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);           // 총 커넥션 수
        cm.setDefaultMaxPerRoute(10);  // 라우트당 최대 커넥션 수

        // 2. HttpClient 생성
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        // 3. RequestFactory 생성
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(60 * 1000);
        requestFactory.setReadTimeout(120 * 1000);

        // 4. RestTemplate 생성
        return new RestTemplate(requestFactory);
    }

    public RestTemplate createRestTemplateWithKey(String accessKey, String secretKey) {
        RestTemplate base = ncpRestTemplate();
        RestTemplate copy = new RestTemplate(base.getRequestFactory());
        copy.setInterceptors(Collections.singletonList(new SigningInterceptor(accessKey, secretKey)));
        return copy;
    }
}
