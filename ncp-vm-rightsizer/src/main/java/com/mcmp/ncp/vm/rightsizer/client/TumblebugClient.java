package com.mcmp.ncp.vm.rightsizer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendCandidateDto;
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
    public void fillCurrentSpec(RecommendCandidateDto candidate) {
        String nsId  = candidate.getTbbNsId();
        String mciId = candidate.getTbbMciId();
        String vmId  = candidate.getTbbVmId();

        if (nsId == null || mciId == null || vmId == null) {
            log.warn("TBB VM 식별자 누락 - instanceNo: {}, nsId: {}, mciId: {}, tbbVmId: {}",
                    candidate.getResourceId(), nsId, mciId, vmId);
            return;
        }

        String url = String.format("%s/ns/%s/mci/%s/vm/%s", tumblebugUrl, nsId, mciId, vmId);
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return;

            // cspSpecName 직접 사용 (specId의 '+' 인코딩 문제 우회)
            String cspSpecName = body.path("cspSpecName").asText(null);
            String regionName  = body.path("region").path("Region").asText(null);
            candidate.setRegionName(regionName);

            if (cspSpecName != null && !cspSpecName.isEmpty()) {
                fillSpecDetail(candidate, nsId, cspSpecName);
            }
        } catch (Exception e) {
            log.warn("TBB VM 조회 실패 - url: {}, cause: {}", url, e.getMessage());
        }
    }

    /**
     * Tumblebug spec 상세 조회 → vCPU, memoryGiB, cspSpecName 채움
     * GET /tumblebug/ns/{nsId}/resources/spec/{specId}
     */
    private void fillSpecDetail(RecommendCandidateDto candidate, String nsId, String specId) {
        String encodedSpecId = specId.replace("+", "%2B");
        String url = String.format("%s/ns/%s/resources/spec/%s", tumblebugUrl, nsId, encodedSpecId);
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return;

            candidate.setCurrentSpecName(body.path("cspSpecName").asText(null));
            candidate.setCurrentVcpu(body.path("vCPU").asInt(0));
            candidate.setCurrentMemGiB(body.path("memoryGiB").asDouble(0));
            log.debug("현재 스펙 조회 - instanceNo: {}, spec: {}, vCPU: {}, mem: {}GiB",
                    candidate.getResourceId(), candidate.getCurrentSpecName(),
                    candidate.getCurrentVcpu(), candidate.getCurrentMemGiB());
        } catch (Exception e) {
            log.warn("TBB spec 상세 조회 실패 - specId: {}, cause: {}", specId, e.getMessage());
        }
    }

    /**
     * 사이즈 추천 요청
     * POST /tumblebug/recommendSpec
     *
     * @return 추천 스펙명 (예: "n2-standard-4"), 실패 시 null
     */
    public String recommendSpec(RecommendCandidateDto candidate) {
        String url = String.format("%s/recommendSpec", tumblebugUrl);

        boolean isUp    = "Up".equals(candidate.getRecommendType());
        int    vcpu     = candidate.getCurrentVcpu()   != null ? candidate.getCurrentVcpu()   : 2;
        double memGiB   = candidate.getCurrentMemGiB() != null ? candidate.getCurrentMemGiB() : 4.0;
        String operator = isUp ? ">=" : "<=";

        int targetVcpu   = isUp ? vcpu * 2                            : Math.max(1, vcpu / 2);
        int targetMemGiB = isUp ? (int) Math.ceil(memGiB * 2)         : (int) Math.max(1.0, Math.floor(memGiB / 2));

        Map<String, Object> body = Map.of(
            "filter", Map.of(
                "policy", List.of(
                    Map.of("metric", "vCPU",      "condition", List.of(Map.of("operand", String.valueOf(targetVcpu),   "operator", operator))),
                    Map.of("metric", "memoryGiB", "condition", List.of(Map.of("operand", String.valueOf(targetMemGiB), "operator", operator)))
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

            // providerName=ncp + 같은 리전 우선 필터링
            String regionName = candidate.getRegionName();
            JsonNode matched = null;

            for (JsonNode spec : resBody) {
                String provider = spec.path("providerName").asText("");
                String region   = spec.path("regionName").asText("");
                if ("ncp".equalsIgnoreCase(provider)) {
                    if (regionName != null && regionName.equalsIgnoreCase(region)) {
                        matched = spec;
                        break;
                    }
                    if (matched == null) matched = spec;
                }
            }

            // ncp 스펙이 없으면 추천 없음
            if (matched == null) return null;

            String specName = matched.path("cspSpecName").asText(null);
            log.info("추천 스펙 - instanceNo: {}, direction: {}, {} → {}",
                    candidate.getResourceId(), candidate.getRecommendType(),
                    candidate.getCurrentSpecName(), specName);
            return specName;
        } catch (Exception e) {
            log.warn("TBB 스펙 추천 실패 - instanceNo: {}, cause: {}", candidate.getResourceId(), e.getMessage());
            return null;
        }
    }
}
