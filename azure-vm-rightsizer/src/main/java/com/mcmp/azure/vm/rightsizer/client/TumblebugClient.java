package com.mcmp.azure.vm.rightsizer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.mcmp.azure.vm.rightsizer.dto.RecommendCandidateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
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

    /** Tumblebug 공용 자원(spec/image) 네임스페이스 */
    private static final String SYSTEM_NS = "system";

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
     * GET /tumblebug/ns/{nsId}/infra/{infraId}/node/{nodeId}
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

        String url = String.format("%s/ns/%s/infra/%s/node/%s", tumblebugUrl, nsId, mciId, vmId);
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return;

            // specId(예: azure+koreacentral+standard_b1s)는 리전까지 특정되므로 우선 사용,
            // 없거나 조회 실패 시 cspSpecName(예: Standard_B1s)으로 재시도
            String specId      = body.path("specId").asText(null);
            String cspSpecName = body.path("cspSpecName").asText(null);
            // Tumblebug node 응답의 region은 {"region": "...", "zone": "..."} (v0.12.25+). 구버전 키(Region)도 허용
            String regionName  = body.path("region").path("region").asText(
                    body.path("region").path("Region").asText(null));
            candidate.setRegionName(regionName);

            boolean filled = specId != null && !specId.isEmpty()
                    && fillSpecDetail(candidate, nsId, specId);
            if (!filled && cspSpecName != null && !cspSpecName.isEmpty() && !cspSpecName.equals(specId)) {
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
    private boolean fillSpecDetail(RecommendCandidateDto candidate, String nsId, String specKey) {
        JsonNode body = getSpec(nsId, specKey);
        if (body == null) {
            log.warn("TBB spec 상세 조회 실패 - specKey: {}, ns: system/{}", specKey, nsId);
            return false;
        }

        candidate.setCurrentSpecName(body.path("cspSpecName").asText(null));
        candidate.setCurrentVcpu(body.path("vCPU").asInt(0));
        candidate.setCurrentMemGiB(body.path("memoryGiB").asDouble(0));
        double rawCost = body.path("costPerHour").asDouble(0);
        candidate.setCurrentCostPerHour(rawCost > 0 ? rawCost : null);
        log.info("현재 스펙 조회 - resourceId: {}, spec: {}, vCPU: {}, mem: {}GiB, cost: ${}/h",
                candidate.getResourceId(), candidate.getCurrentSpecName(),
                candidate.getCurrentVcpu(), candidate.getCurrentMemGiB(),
                candidate.getCurrentCostPerHour());
        return true;
    }

    /**
     * Tumblebug spec 조회 (system 네임스페이스 우선, 실패 시 VM 네임스페이스 재시도)
     * GET /tumblebug/ns/{nsId}/resources/spec/{specKey}
     *
     * Tumblebug은 CSP에서 자동 수집한 spec을 공용 네임스페이스(system)에 저장하고
     * node의 specId도 system의 spec을 가리키므로 system을 먼저 조회한다.
     * 사용자가 자기 네임스페이스에 직접 등록한 spec을 위해 VM 네임스페이스로 한 번 더 조회한다.
     *
     * @param vmNsId  VM(node)이 속한 네임스페이스
     * @param specKey specId(예: azure+koreacentral+standard_b1s) 또는 cspSpecName(예: Standard_B1s)
     * @return spec JSON, 두 네임스페이스 모두에 없으면 null
     */
    private JsonNode getSpec(String vmNsId, String specKey) {
        List<String> namespaces = (vmNsId == null || vmNsId.isEmpty() || SYSTEM_NS.equalsIgnoreCase(vmNsId))
                ? List.of(SYSTEM_NS)
                : List.of(SYSTEM_NS, vmNsId);

        for (String ns : namespaces) {
            // specId의 '+'는 경로에서 그대로 유효하므로 인코딩하지 않고 URI로 전달 (RestTemplate 재인코딩 방지)
            String url = String.format("%s/ns/%s/resources/spec/%s", tumblebugUrl, ns, specKey);
            try {
                ResponseEntity<JsonNode> res = restTemplate.exchange(
                        URI.create(url), HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
                JsonNode body = res.getBody();
                if (body != null && !body.isMissingNode() && !body.isNull()) {
                    return body;
                }
            } catch (Exception e) {
                log.debug("TBB spec 조회 실패 - ns: {}, specKey: {}, cause: {}", ns, specKey, e.getMessage());
            }
        }
        return null;
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
        JsonNode body = getSpec(nsId, specName);
        return body == null ? null : body.path("cspSpecName").asText(null);
    }
}
