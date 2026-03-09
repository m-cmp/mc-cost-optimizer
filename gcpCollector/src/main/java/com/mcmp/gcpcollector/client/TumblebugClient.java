package com.mcmp.gcpcollector.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.mcmp.gcpcollector.dto.GcpVmRightSizeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TumblebugClient {

    @Value("${tumblebug.url}")
    private String tumblebugUrl;

    @Value("${tumblebug.username}")
    private String username;

    @Value("${tumblebug.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders buildHeaders() {
        String auth = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Tumblebug VM 상세 조회 → specId, vCPU, memoryGiB, regionName 채움
     * GET /tumblebug/ns/{nsId}/mci/{mciId}/vm/{vmId}
     */
    public void fillCurrentSpec(GcpVmRightSizeDto vm) {
        String nsId  = vm.getTbbNsId();
        String mciId = vm.getTbbMciId();
        String vmId  = vm.getTbbVmId();

        if (nsId == null || mciId == null || vmId == null) {
            log.warn("TBB VM 식별자 누락 - vmId: {}, nsId: {}, mciId: {}, tbbVmId: {}",
                    vm.getVmId(), nsId, mciId, vmId);
            return;
        }

        String url = String.format("%s/ns/%s/mci/%s/vm/%s", tumblebugUrl, nsId, mciId, vmId);
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return;

            String specId = body.path("specId").asText(null);
            vm.setCurrentSpecId(specId);

            // 리전 추출 (추천 결과 필터용)
            String regionName = body.path("region").path("Region").asText(null);
            vm.setRegionName(regionName);

            if (specId != null && !specId.isEmpty()) {
                fillSpecDetail(vm, nsId, specId);
            }
        } catch (Exception e) {
            log.warn("TBB VM 조회 실패 - url: {}, cause: {}", url, e.getMessage());
        }
    }

    /**
     * Tumblebug spec 상세 조회 → vCPU, memoryGiB 채움
     * GET /tumblebug/ns/{nsId}/resources/spec/{specId}
     */
    private void fillSpecDetail(GcpVmRightSizeDto vm, String nsId, String specId) {
        String url = String.format("%s/ns/%s/resources/spec/%s", tumblebugUrl, nsId, specId);
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return;

            vm.setCurrentSpecName(body.path("cspSpecName").asText(null));
            vm.setCurrentVcpu(body.path("vCPU").asInt(0));
            vm.setCurrentMemoryGiB(body.path("memoryGiB").asDouble(0));
            log.debug("현재 스펙 조회 - vmId: {}, spec: {}, vCPU: {}, mem: {}GiB",
                    vm.getVmId(), vm.getCurrentSpecName(), vm.getCurrentVcpu(), vm.getCurrentMemoryGiB());
        } catch (Exception e) {
            log.warn("TBB spec 상세 조회 실패 - specId: {}, cause: {}", specId, e.getMessage());
        }
    }

    /**
     * 사이즈 추천 요청
     * POST /tumblebug/recommendSpec
     *
     * @return 추천 스펙명 (예: "e2-standard-4"), 실패 시 null
     */
    public String recommendSpec(GcpVmRightSizeDto vm) {
        String url = String.format("%s/recommendSpec", tumblebugUrl);

        boolean isUp = "Up".equals(vm.getRecommendType());
        int    vcpu   = vm.getCurrentVcpu()    != null ? vm.getCurrentVcpu()    : 2;
        double memGiB = vm.getCurrentMemoryGiB() != null ? vm.getCurrentMemoryGiB() : 1.0;

        int targetVcpu   = isUp ? vcpu * 2                             : Math.max(1, vcpu / 2);
        int targetMemGiB = isUp ? (int) Math.ceil(memGiB * 2)         : (int) Math.max(1.0, Math.floor(memGiB / 2));
        String operator  = isUp ? ">="                                 : "<=";

        Map<String, Object> body = Map.of(
            "filter", Map.of(
                "policy", List.of(
                    Map.of("metric", "vCPU",      "condition", List.of(Map.of("operand", String.valueOf(targetVcpu),    "operator", operator))),
                    Map.of("metric", "memoryGiB", "condition", List.of(Map.of("operand", String.valueOf(targetMemGiB),  "operator", operator)))
                )
            ),
            "priority", Map.of(
                "policy", List.of(Map.of("metric", "cost", "weight", "0.5"))
            ),
            "limit", "5"
        );

        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.POST,
                    new HttpEntity<>(body, buildHeaders()),
                    JsonNode.class);
            JsonNode resBody = res.getBody();
            if (resBody == null || !resBody.isArray() || resBody.isEmpty()) return null;

            // providerName=gcp + 같은 리전 우선 필터링
            String regionName = vm.getRegionName();
            JsonNode matched = null;

            for (JsonNode spec : resBody) {
                String provider = spec.path("providerName").asText("");
                String region   = spec.path("regionName").asText("");
                if ("gcp".equalsIgnoreCase(provider)) {
                    // 같은 리전이면 우선 선택
                    if (regionName != null && regionName.equalsIgnoreCase(region)) {
                        matched = spec;
                        break;
                    }
                    // 같은 리전 없으면 첫 번째 gcp 스펙 저장
                    if (matched == null) matched = spec;
                }
            }

            // gcp 스펙이 하나도 없으면 전체 첫 번째
            if (matched == null) matched = resBody.path(0);

            String specId   = matched.path("id").asText(null);
            String specName = matched.path("cspSpecName").asText(null);
            vm.setRecommendSpecId(specId);
            vm.setRecommendSpecName(specName);

            log.info("추천 스펙 - vmId: {}, direction: {}, {} → {}",
                    vm.getVmId(), vm.getRecommendType(), vm.getCurrentSpecName(), specName);
            return specName;
        } catch (Exception e) {
            log.warn("TBB 스펙 추천 실패 - vmId: {}, cause: {}", vm.getVmId(), e.getMessage());
            return null;
        }
    }
}
