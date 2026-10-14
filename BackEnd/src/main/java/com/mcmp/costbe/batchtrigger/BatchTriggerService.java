package com.mcmp.costbe.batchtrigger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CSP별 미사용/이상비용/자원추천 배치를 Quartz cron 주기를 기다리지 않고 즉시 실행하기 위한 오케스트레이터.
 * 각 배치가 직접 AlarmService 로 알림을 발송하므로, 여기서는 각 모듈의 즉시실행 엔드포인트를
 * 순서대로 호출만 하고 결과는 취합하지 않는다(호출 성공/실패 여부만 반환).
 */
@Slf4j
@Service
public class BatchTriggerService {

    @Value("${costopti.costprocessor.url}")
    private String costProcessorUrl;

    @Value("${costopti.costcollector.url}")
    private String costCollectorUrl;

    @Value("${costopti.azurerightsizer.url}")
    private String azureRightsizerUrl;

    @Value("${costopti.ncprightsizer.url}")
    private String ncpRightsizerUrl;

    @Value("${costopti.azurecollector.url}")
    private String azureCollectorUrl;

    @Value("${costopti.ncpcollector.url}")
    private String ncpCollectorUrl;

    @Value("${costopti.gcpcollector.url}")
    private String gcpCollectorUrl;

    private final RestTemplate restTemplate;

    public BatchTriggerService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        // AWS unusedProcessJob 은 리소스 건별 동기 호출이라 다른 배치보다 오래 걸릴 수 있음
        factory.setReadTimeout(120000);
        this.restTemplate = new RestTemplate(factory);
    }

    public Map<String, String> triggerAws() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("unused", callGet(costProcessorUrl + "/api/costopti/costprs/admin/unused/run"));
        result.put("abnormal", callGet(costProcessorUrl + "/api/costopti/costprs/admin/abnormal/run"));
        result.put("budget", callGet(costCollectorUrl + "/api/costopti/costctl/admin/budget/check"));
        return result;
    }

    public Map<String, String> triggerAzure() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("unused", callGet(azureRightsizerUrl + "/api/batch/azure/unused"));
        result.put("anomaly", callGet(azureRightsizerUrl + "/api/batch/azure/anomaly"));
        result.put("recommend", callGet(azureRightsizerUrl + "/api/batch/azure/recommend"));
        result.put("budget", callGet(azureCollectorUrl + "/api/admin/budget/check"));
        return result;
    }

    public Map<String, String> triggerNcp() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("unused", callGet(ncpRightsizerUrl + "/api/batch/ncp/unused"));
        result.put("anomaly", callGet(ncpRightsizerUrl + "/api/batch/ncp/anomaly"));
        result.put("recommend", callGet(ncpRightsizerUrl + "/api/batch/ncp/recommend"));
        result.put("budget", callGet(ncpCollectorUrl + "/api/admin/budget/check"));
        return result;
    }

    public Map<String, String> triggerGcp() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("unused", callGet(gcpCollectorUrl + "/admin/unused/detect"));
        result.put("anomaly", callGet(gcpCollectorUrl + "/admin/anomaly/detect"));
        result.put("recommend", callGet(gcpCollectorUrl + "/admin/rightsize/detect"));
        result.put("budget", callGet(gcpCollectorUrl + "/admin/budget/check"));
        return result;
    }

    private String callGet(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return "OK: " + response.getBody();
        } catch (Exception e) {
            log.error("Batch trigger call failed - url: {}", url, e);
            return "FAILED: " + e.getMessage();
        }
    }
}
