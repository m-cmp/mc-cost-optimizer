package com.mcmp.azure.vm.rightsizer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.mcmp.azure.vm.rightsizer.dto.RecommendCandidateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            log.warn("TBB VM 식별자 누락 - resourceId: {}, nsId: {}, mciId: {}, tbbVmId: {}",
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
            double rawCost = body.path("costPerHour").asDouble(0);
            candidate.setCurrentCostPerHour(rawCost > 0 ? rawCost : null);
            log.info("현재 스펙 조회 - resourceId: {}, spec: {}, vCPU: {}, mem: {}GiB, cost: ${}/h",
                    candidate.getResourceId(), candidate.getCurrentSpecName(),
                    candidate.getCurrentVcpu(), candidate.getCurrentMemGiB(),
                    candidate.getCurrentCostPerHour());
        } catch (Exception e) {
            log.warn("TBB spec 상세 조회 실패 - specId: {}, cause: {}", specId, e.getMessage());
        }
    }

    /**
     * 사이즈 추천 요청
     * POST /tumblebug/recommendSpec
     *
     * @return 추천 스펙명 (예: "Standard_D4s_v3"), 실패 시 null
     */
    public String recommendSpec(RecommendCandidateDto candidate) {
        String url = String.format("%s/recommendSpec", tumblebugUrl);

        if (candidate.getCurrentVcpu() == null || candidate.getCurrentMemGiB() == null) {
            log.warn("현재 스펙 정보 없음 - 추천 불가, resourceId: {}", candidate.getResourceId());
            return null;
        }

        boolean isUp     = "Up".equals(candidate.getRecommendType());
        int     vcpu     = candidate.getCurrentVcpu();
        double  memGiB   = candidate.getCurrentMemGiB();
        Double  costData = candidate.getCurrentCostPerHour(); // null이면 cost 데이터 없음

        List<Map<String, Object>> filterPolicy;
        if (isUp) {
            // Up: 현재보다 vCPU 많고, memGiB 같거나 많은 것 중 cost ASC
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(vcpu + 1), "operator", ">="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),   "operator", ">="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "azure",                  "operator", "=")))
            );
        } else if (costData != null && costData > 0) {
            // Down + cost 데이터 있음: vCPU/memGiB/costPerHour 모두 필터
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(Math.max(1, vcpu - 1)), "operator", "<="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),               "operator", "<="))),
                Map.of("metric", "costPerHour",  "condition", List.of(Map.of("operand", String.valueOf(costData),             "operator", "<="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "azure",                              "operator", "=")))
            );
        } else {
            // Down + cost 데이터 없음: vCPU/memGiB 기준으로만 필터
            log.warn("cost 데이터 없음 - vCPU/memGiB 기준으로만 추천, resourceId: {}", candidate.getResourceId());
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(Math.max(1, vcpu - 1)), "operator", "<="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),               "operator", "<="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "azure",                              "operator", "=")))
            );
        }

        Map<String, Object> body = Map.of(
            "filter", Map.of("policy", filterPolicy),
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

            // providerName=azure + 같은 리전 우선 필터링
            String regionName = candidate.getRegionName();
            JsonNode matched = null;

            for (JsonNode spec : resBody) {
                String provider = spec.path("providerName").asText("");
                String region   = spec.path("regionName").asText("");
                if ("azure".equalsIgnoreCase(provider)) {
                    if (regionName != null && regionName.equalsIgnoreCase(region)) {
                        matched = spec;
                        break;
                    }
                    if (matched == null) matched = spec;
                }
            }

            // azure 스펙이 없으면 추천 없음
            if (matched == null) return null;

            String specName = matched.path("cspSpecName").asText(null);
            double rawRecommendCost = matched.path("costPerHour").asDouble(0);
            candidate.setRecommendCostPerHour(rawRecommendCost > 0 ? rawRecommendCost : null);
            log.info("추천 스펙 - resourceId: {}, direction: {}, {} → {}, cost: ${}/h → ${}/h",
                    candidate.getResourceId(), candidate.getRecommendType(),
                    candidate.getCurrentSpecName(), specName,
                    candidate.getCurrentCostPerHour(), candidate.getRecommendCostPerHour());
            return specName;
        } catch (Exception e) {
            log.warn("TBB 스펙 추천 실패 - resourceId: {}, cause: {}", candidate.getResourceId(), e.getMessage());
            return null;
        }
    }

    /**
     * Modernize 추천 - 다음 세대 스펙명 계산 후 Tumblebug 존재 여부 확인
     * Azure 규칙: _v{N} → _v{N+1}, 버전 없으면(1세대) → _v2
     */
    public String findModernizeSpec(RecommendCandidateDto candidate) {
        String nsId           = candidate.getTbbNsId();
        String currentSpecName = candidate.getInstanceType();
        if (nsId == null || currentSpecName == null) return null;

        String nextGenSpecName = resolveNextGenSpecName(currentSpecName);
        if (nextGenSpecName == null) return null;

        String found = tryGetSpec(nsId, nextGenSpecName);
        log.info("Modernize 추천 - resourceId: {}, {} → {}",
                candidate.getResourceId(), currentSpecName, found != null ? found : "없음");
        return found;
    }

    private String resolveNextGenSpecName(String currentSpecName) {
        // Standard_D2s_v3 → Standard_D2s_v4
        // Standard_D2s    → Standard_D2s_v2  (v1 생략 케이스)
        Pattern withV = Pattern.compile("^(.+)_v(\\d+)$", Pattern.CASE_INSENSITIVE);
        Matcher m = withV.matcher(currentSpecName);
        if (m.matches()) {
            return m.group(1) + "_v" + (Integer.parseInt(m.group(2)) + 1);
        }
        return currentSpecName + "_v2";
    }

    private String tryGetSpec(String nsId, String specName) {
        String url = String.format("%s/ns/%s/resources/spec/%s",
                tumblebugUrl, nsId, specName.replace("+", "%2B"));
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return null;
            return body.path("cspSpecName").asText(null);
        } catch (Exception e) {
            log.debug("스펙 존재 확인 실패 - specName: {}, cause: {}", specName, e.getMessage());
            return null;
        }
    }
}
