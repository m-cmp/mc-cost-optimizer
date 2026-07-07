package com.mcmp.costselector.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.mcmp.costselector.unused.model.UnusedResourceStatusModel;
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
     * Tumblebug Node(VM) 상세 조회 → currentSpecName, currentVcpu, currentMemGiB, tbbRegionName 채움
     * GET /tumblebug/ns/{nsId}/infra/{infraId}/node/{nodeId}
     */
    public void fillCurrentSpec(UnusedResourceStatusModel rscStatus) {
        String nsId  = rscStatus.getTbbNsId();
        String mciId = rscStatus.getTbbMciId();
        String vmId  = rscStatus.getTbbVmId();

        if (nsId == null || mciId == null || vmId == null) {
            log.warn("TBB VM 식별자 누락 - resourceId: {}", rscStatus.getResource_id());
            return;
        }

        String url = String.format("%s/ns/%s/infra/%s/node/%s", tumblebugUrl, nsId, mciId, vmId);
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return;

            // cspSpecName 직접 사용 (specId의 '+' 인코딩 문제 우회)
            String cspSpecName = body.path("cspSpecName").asText(null);
            String regionName  = body.path("region").path("Region").asText(null);
            rscStatus.setTbbRegionName(regionName);

            if (cspSpecName != null && !cspSpecName.isEmpty()) {
                fillSpecDetail(rscStatus, nsId, cspSpecName);
            }
        } catch (Exception e) {
            log.warn("TBB VM 조회 실패 - url: {}, cause: {}", url, e.getMessage());
        }
    }

    private void fillSpecDetail(UnusedResourceStatusModel rscStatus, String nsId, String specId) {
        String encodedSpecId = specId.replace("+", "%2B");
        String url = String.format("%s/ns/%s/resources/spec/%s", tumblebugUrl, nsId, encodedSpecId);
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return;

            rscStatus.setCurrentSpecName(body.path("cspSpecName").asText(null));
            rscStatus.setCurrentVcpu(body.path("vCPU").asInt(0));
            rscStatus.setCurrentMemGiB(body.path("memoryGiB").asDouble(0));
            double rawCost = body.path("costPerHour").asDouble(0);
            rscStatus.setCurrentCostPerHour(rawCost > 0 ? rawCost : null);
            log.info("현재 스펙 조회 - resourceId: {}, spec: {}, vCPU: {}, mem: {}GiB, cost: ${}/h",
                    rscStatus.getResource_id(), rscStatus.getCurrentSpecName(),
                    rscStatus.getCurrentVcpu(), rscStatus.getCurrentMemGiB(),
                    rscStatus.getCurrentCostPerHour());
        } catch (Exception e) {
            log.warn("TBB spec 상세 조회 실패 - specId: {}, cause: {}", specId, e.getMessage());
        }
    }

    /**
     * 사이즈 추천 요청 (Up/Down)
     * POST /tumblebug/recommendSpec
     *
     * @param direction "Up" or "Down"
     * @return 추천 instType (cspSpecName), 실패 시 null
     */
    public String recommendSpec(UnusedResourceStatusModel rscStatus, String direction) {
        String url = String.format("%s/recommendSpec", tumblebugUrl);

        if (rscStatus.getCurrentVcpu() == null || rscStatus.getCurrentMemGiB() == null) {
            log.warn("현재 스펙 정보 없음 - 추천 불가, resourceId: {}", rscStatus.getResource_id());
            return null;
        }

        boolean isUp     = "Up".equals(direction);
        int     vcpu     = rscStatus.getCurrentVcpu();
        double  memGiB   = rscStatus.getCurrentMemGiB();
        Double  costData = rscStatus.getCurrentCostPerHour(); // null이면 cost 데이터 없음

        List<Map<String, Object>> filterPolicy;
        if (isUp) {
            // Up: 현재보다 vCPU 많고, memGiB 같거나 많은 것 중 cost ASC
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(vcpu + 1), "operator", ">="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),   "operator", ">="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "aws",                    "operator", "=")))
            );
        } else if (costData != null && costData > 0) {
            // Down + cost 데이터 있음: vCPU/memGiB/costPerHour 모두 필터
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(Math.max(1, vcpu - 1)), "operator", "<="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),               "operator", "<="))),
                Map.of("metric", "costPerHour",  "condition", List.of(Map.of("operand", String.valueOf(costData),             "operator", "<="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "aws",                               "operator", "=")))
            );
        } else {
            // Down + cost 데이터 없음: vCPU/memGiB 기준으로만 필터
            log.warn("cost 데이터 없음 - vCPU/memGiB 기준으로만 추천, resourceId: {}", rscStatus.getResource_id());
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(Math.max(1, vcpu - 1)), "operator", "<="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),               "operator", "<="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "aws",                               "operator", "=")))
            );
        }

        Map<String, Object> body = Map.of(
            "filter", Map.of("policy", filterPolicy),
            "priority", Map.of(
                "policy", List.of(Map.of("metric", "cost", "weight", 0.5))
            ),
            "limit", 5
        );

        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.POST,
                    new HttpEntity<>(body, buildHeaders()),
                    JsonNode.class);
            JsonNode resBody = res.getBody();
            if (resBody == null || !resBody.isArray() || resBody.isEmpty()) return null;

            // providerName=aws + 같은 리전 우선 필터링
            String regionName = rscStatus.getTbbRegionName();
            JsonNode matched = null;

            for (JsonNode spec : resBody) {
                String provider = spec.path("providerName").asText("");
                String region   = spec.path("regionName").asText("");
                if ("aws".equalsIgnoreCase(provider)) {
                    if (regionName != null && regionName.equalsIgnoreCase(region)) {
                        matched = spec;
                        break;
                    }
                    if (matched == null) matched = spec;
                }
            }

            // aws 스펙이 없으면 추천 없음
            if (matched == null) return null;

            String specName = matched.path("cspSpecName").asText(null);
            double rawRecommendCost = matched.path("costPerHour").asDouble(0);
            rscStatus.setRecommendCostPerHour(rawRecommendCost > 0 ? rawRecommendCost : null);
            log.info("추천 스펙 - resourceId: {}, direction: {}, {} → {}, cost: ${}/h → ${}/h",
                    rscStatus.getResource_id(), direction,
                    rscStatus.getCurrentSpecName(), specName,
                    rscStatus.getCurrentCostPerHour(), rscStatus.getRecommendCostPerHour());
            return specName;
        } catch (Exception e) {
            log.warn("TBB 스펙 추천 실패 - resourceId: {}, cause: {}", rscStatus.getResource_id(), e.getMessage());
            return null;
        }
    }

    /**
     * Modernize 추천 - 다음 세대 스펙명 계산 후 Tumblebug 존재 여부 확인
     * AWS 규칙: [family][gen][variant].[size] → gen+1, 같은 variant 유지
     * variant 없는 구세대(e.g. m5)는 신세대에서 'i' suffix 필수 → fallback으로 'm6i' 시도
     */
    public String findModernizeSpec(UnusedResourceStatusModel rscStatus) {
        String nsId            = rscStatus.getTbbNsId();
        String currentSpecName = rscStatus.getInstance_type();
        if (nsId == null || currentSpecName == null) return null;

        Pattern p = Pattern.compile("^([a-z]+)(\\d+)([a-z]*)\\.(\\S+)$");
        Matcher m = p.matcher(currentSpecName.toLowerCase());
        if (!m.matches()) return null;

        String family  = m.group(1);
        int    gen     = Integer.parseInt(m.group(2));
        String variant = m.group(3);
        String size    = m.group(4);

        // 1차: 동일 variant로 시도 (e.g. m5.large → m6.large, c6i.large → c7i.large)
        String nextGen = family + (gen + 1) + variant + "." + size;
        String found   = tryGetSpec(nsId, nextGen);

        // 2차: variant 없는 구세대이면 Intel(i) suffix fallback (e.g. m6.large 없으면 m6i.large)
        if (found == null && variant.isEmpty()) {
            String fallback = family + (gen + 1) + "i." + size;
            found = tryGetSpec(nsId, fallback);
            if (found != null) nextGen = fallback;
        }

        log.info("Modernize 추천 - resourceId: {}, {} → {}",
                rscStatus.getResource_id(), currentSpecName, found != null ? found : "없음");
        return found;
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
