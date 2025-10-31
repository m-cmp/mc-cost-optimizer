package com.mcmp.cost.ncp.collector.config;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class SigningInterceptor implements ClientHttpRequestInterceptor {

    private final String accessKey;
    private final String secretKey;

    public SigningInterceptor(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        URI uri = request.getURI();
        String pathWithQuery = uri.getRawPath();
        if (uri.getRawQuery() != null) {
            pathWithQuery = pathWithQuery + "?" + uri.getRawQuery();
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String message = request.getMethod() + " " + pathWithQuery + "\n" +
                timestamp + "\n" +
                accessKey;

        // message → 서명값(signature) 생성 후 header에 추가
        String signature = sign(message);
        request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        request.getHeaders().add("x-ncp-apigw-timestamp", timestamp);
        request.getHeaders().add("x-ncp-iam-access-key", accessKey);
        request.getHeaders().add("x-ncp-apigw-signature-v2", signature);
        return execution.execute(request, body);
    }

    private String sign(String message) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to make signature for IAM credentials: " + e.getMessage(), e);
        }
    }
}
