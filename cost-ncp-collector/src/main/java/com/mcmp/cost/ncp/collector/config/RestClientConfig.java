package com.mcmp.cost.ncp.collector.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean(name = "ncpRequestFactory")
    public HttpComponentsClientHttpRequestFactory ncpRequestFactory() {
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
        requestFactory.setConnectTimeout(Duration.ofSeconds(60));
        requestFactory.setReadTimeout(Duration.ofSeconds(120));

        return requestFactory;
    }

    public RestClient createRestClientWithKey(String accessKey, String secretKey) {
        return RestClient.builder()
                .requestFactory(ncpRequestFactory())
                .requestInterceptor(new SigningInterceptor(accessKey, secretKey))
                .build();
    }
}
