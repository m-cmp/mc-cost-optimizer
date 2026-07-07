package com.mcmp.costbe.alarm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * 알람 서비스(AlarmService, 내부 :9000) 로 향하는 요청을 BE(:9090) 가 서버사이드로 대신 호출하는 리버스 프록시.
 *
 * 브라우저(특히 포털 iframe)가 알람 서비스의 자체 서명 인증서(:9000) 를 직접 호출하면
 * ERR_CERT_AUTHORITY_INVALID 로 차단된다. 프론트가 이미 신뢰된 BE origin 의
 * /api/costopti/be/alert/** 로 호출하면, BE 가 내부 HTTP 로 알람 서비스에 포워딩하므로
 * 브라우저는 :9000 을 전혀 만지지 않는다.
 *
 * method / query string / body / status code / content-type 를 그대로 중계한다.
 */
@RestController
@RequestMapping("/api/costopti/be/alert")
@Slf4j
public class AlertProxyController {

    private static final String PROXY_PREFIX = "/api/costopti/be/alert";
    private static final String ALARM_BASE_PATH = "/api/costopti/alert";

    @Value("${costopti.alarmservice.url}")
    private String alarmServiceUrl;

    private final RestTemplate restTemplate;

    public AlertProxyController() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(20000);
        this.restTemplate = new RestTemplate(factory);
        // 알람 서비스는 4xx/에러 메시지도 200 이 아닌 상태로 돌려줄 수 있으므로,
        // 예외로 터뜨리지 않고 상태/바디를 그대로 중계하기 위해 에러 핸들러를 무력화한다.
        this.restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }
        });
    }

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request,
                                        @RequestBody(required = false) byte[] body) {
        String subPath = request.getRequestURI().substring(PROXY_PREFIX.length()); // 예: /insertSlackToken
        String query = request.getQueryString();
        // getQueryString() 은 이미 인코딩된 원문이므로 URI 객체로 넘겨 RestTemplate 의 재인코딩을 방지한다.
        String targetUrl = alarmServiceUrl + ALARM_BASE_PATH + subPath
                + (query != null ? "?" + query : "");

        HttpHeaders headers = new HttpHeaders();
        String contentType = request.getContentType();
        if (contentType != null) {
            headers.set(HttpHeaders.CONTENT_TYPE, contentType);
        }
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        if (accept != null) {
            headers.set(HttpHeaders.ACCEPT, accept);
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> upstream =
                    restTemplate.exchange(URI.create(targetUrl), method, entity, byte[].class);

            HttpHeaders respHeaders = new HttpHeaders();
            MediaType respContentType = upstream.getHeaders().getContentType();
            if (respContentType != null) {
                respHeaders.setContentType(respContentType);
            }
            return new ResponseEntity<>(upstream.getBody(), respHeaders, upstream.getStatusCode());
        } catch (Exception e) {
            log.error("[AlertProxy] failed to proxy {} {} -> {}", method, subPath, e.getMessage());
            return new ResponseEntity<>(
                    ("Alarm service proxy error: " + e.getMessage()).getBytes(StandardCharsets.UTF_8),
                    HttpStatus.BAD_GATEWAY);
        }
    }
}
