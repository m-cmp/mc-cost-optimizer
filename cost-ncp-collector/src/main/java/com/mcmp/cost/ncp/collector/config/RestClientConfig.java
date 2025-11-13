package com.mcmp.cost.ncp.collector.config;

import com.mcmp.cost.ncp.collector.properties.NcpSslProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final NcpSslProperties ncpSslProperties;

    @Bean(name = "ncpRequestFactory")
    public HttpComponentsClientHttpRequestFactory ncpRequestFactory() {
        PoolingHttpClientConnectionManager cm;
        CloseableHttpClient httpClient;

        if (ncpSslProperties.isDisabled()) {
            // SSL 검증 비활성화 (개발 환경용)
            try {
                SSLContext sslContext = SSLContextBuilder.create()
                        .loadTrustMaterial(null, (chain, authType) -> true)
                        .build();

                SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(sslContext)
                        .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .build();

                cm = PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(sslSocketFactory)
                        .setMaxConnTotal(100)
                        .setMaxConnPerRoute(10)
                        .build();

                httpClient = HttpClients.custom()
                        .setConnectionManager(cm)
                        .build();

                log.warn("NCP RestClient configured with SSL verification DISABLED. This is for development only!");

            } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
                log.error("Failed to configure insecure SSL context, falling back to default", e);
                cm = PoolingHttpClientConnectionManagerBuilder.create()
                        .setMaxConnTotal(100)
                        .setMaxConnPerRoute(10)
                        .build();
                httpClient = HttpClients.custom()
                        .setConnectionManager(cm)
                        .build();
            }
        } else {
            // 기본 SSL 검증 활성화 (운영 환경용)
            cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setMaxConnTotal(100)
                    .setMaxConnPerRoute(10)
                    .build();
            httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
            log.info("NCP RestClient configured with SSL verification ENABLED");
        }

        // RequestFactory 생성
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
