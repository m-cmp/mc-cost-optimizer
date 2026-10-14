package com.mcmp.gcpcollector.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.mcmp.gcpcollector.dto.GcpVmRightSizeDto;
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
import java.util.Set;
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
    public void fillCurrentSpec(GcpVmRightSizeDto vm) {
        String nsId  = vm.getTbbNsId();
        String mciId = vm.getTbbMciId();
        String vmId  = vm.getTbbVmId();

        if (nsId == null || mciId == null || vmId == null) {
            log.warn("TBB VM 식별자 누락 - vmId: {}, nsId: {}, mciId: {}, tbbVmId: {}",
                    vm.getVmId(), nsId, mciId, vmId);
            return;
        }

        String url = String.format("%s/ns/%s/infra/%s/node/%s", tumblebugUrl, nsId, mciId, vmId);
        try {
            ResponseEntity<JsonNode> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(buildHeaders()), JsonNode.class);
            JsonNode body = res.getBody();
            if (body == null) return;

            // specId(예: gcp+asia-northeast3+e2-medium)는 리전까지 특정되므로 우선 사용,
            // 없거나 조회 실패 시 cspSpecName(예: e2-medium)으로 재시도
            String specId      = body.path("specId").asText(null);
            String cspSpecName = body.path("cspSpecName").asText(null);
            vm.setCurrentSpecId(specId);

            // 리전 추출 (추천 결과 필터용)
            // Tumblebug node 응답의 region은 {"region": "...", "zone": "..."} (v0.12.25+). 구버전 키(Region)도 허용
            String regionName = body.path("region").path("region").asText(
                    body.path("region").path("Region").asText(null));
            vm.setRegionName(regionName);

            boolean filled = specId != null && !specId.isEmpty()
                    && fillSpecDetail(vm, nsId, specId);
            if (!filled && cspSpecName != null && !cspSpecName.isEmpty() && !cspSpecName.equals(specId)) {
                fillSpecDetail(vm, nsId, cspSpecName);
            }
        } catch (Exception e) {
            log.warn("TBB VM 조회 실패 - url: {}, cause: {}", url, e.getMessage());
        }
    }

    /**
     * Tumblebug spec 상세 조회 → vCPU, memoryGiB 채움
     * GET /tumblebug/ns/{nsId}/resources/spec/{specId}
     */
    private boolean fillSpecDetail(GcpVmRightSizeDto vm, String nsId, String specKey) {
        JsonNode body = getSpec(nsId, specKey);
        if (body == null) {
            log.warn("TBB spec 상세 조회 실패 - specKey: {}, ns: system/{}", specKey, nsId);
            return false;
        }

        vm.setCurrentSpecName(body.path("cspSpecName").asText(null));
        vm.setCurrentVcpu(body.path("vCPU").asInt(0));
        vm.setCurrentMemoryGiB(body.path("memoryGiB").asDouble(0));
        double rawCost = body.path("costPerHour").asDouble(0);
        vm.setCurrentCostPerHour(rawCost > 0 ? rawCost : null);
        log.info("현재 스펙 조회 - vmId: {}, spec: {}, vCPU: {}, mem: {}GiB, cost: ${}/h",
                vm.getVmId(), vm.getCurrentSpecName(), vm.getCurrentVcpu(), vm.getCurrentMemoryGiB(),
                vm.getCurrentCostPerHour());
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
     * @param specKey specId(예: gcp+asia-northeast3+e2-medium) 또는 cspSpecName(예: e2-medium)
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
     * @return 추천 스펙명 (예: "e2-standard-4"), 실패 시 null
     */
    public String recommendSpec(GcpVmRightSizeDto vm) {
        String url = String.format("%s/recommendSpec", tumblebugUrl);

        if (vm.getCurrentVcpu() == null || vm.getCurrentMemoryGiB() == null) {
            log.warn("현재 스펙 정보 없음 - 추천 불가, vmId: {}", vm.getVmId());
            return null;
        }

        boolean isUp     = "Up".equals(vm.getRecommendType());
        int     vcpu     = vm.getCurrentVcpu();
        double  memGiB   = vm.getCurrentMemoryGiB();
        Double  costData = vm.getCurrentCostPerHour(); // null이면 cost 데이터 없음

        List<Map<String, Object>> filterPolicy;
        if (isUp) {
            // Up: 현재보다 vCPU 많고, memGiB 같거나 많은 것 중 cost ASC
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(vcpu + 1), "operator", ">="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),   "operator", ">="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "gcp",                    "operator", "=")))
            );
        } else if (costData != null && costData > 0) {
            // Down + cost 데이터 있음: vCPU/memGiB/costPerHour 모두 필터
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(Math.max(1, vcpu - 1)), "operator", "<="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),               "operator", "<="))),
                Map.of("metric", "costPerHour",  "condition", List.of(Map.of("operand", String.valueOf(costData),             "operator", "<="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "gcp",                               "operator", "=")))
            );
        } else {
            // Down + cost 데이터 없음: vCPU/memGiB 기준으로만 필터
            log.warn("cost 데이터 없음 - vCPU/memGiB 기준으로만 추천, vmId: {}", vm.getVmId());
            filterPolicy = List.of(
                Map.of("metric", "vCPU",         "condition", List.of(Map.of("operand", String.valueOf(Math.max(1, vcpu - 1)), "operator", "<="))),
                Map.of("metric", "memoryGiB",    "condition", List.of(Map.of("operand", String.valueOf(memGiB),               "operator", "<="))),
                Map.of("metric", "providerName", "condition", List.of(Map.of("operand", "gcp",                               "operator", "=")))
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

            // gcp 스펙이 없으면 추천 없음
            if (matched == null) return null;

            String specId   = matched.path("id").asText(null);
            String specName = matched.path("cspSpecName").asText(null);
            vm.setRecommendSpecId(specId);
            vm.setRecommendSpecName(specName);
            double rawRecommendCost = matched.path("costPerHour").asDouble(0);
            vm.setRecommendCostPerHour(rawRecommendCost > 0 ? rawRecommendCost : null);

            log.info("추천 스펙 - vmId: {}, direction: {}, {} → {}, cost: ${}/h → ${}/h",
                    vm.getVmId(), vm.getRecommendType(), vm.getCurrentSpecName(), specName,
                    vm.getCurrentCostPerHour(), vm.getRecommendCostPerHour());
            return specName;
        } catch (Exception e) {
            log.warn("TBB 스펙 추천 실패 - vmId: {}, cause: {}", vm.getVmId(), e.getMessage());
            return null;
        }
    }

    /**
     * Modernize 추천 - 다음 세대 스펙명 계산 후 Tumblebug 존재 여부 확인
     * GCP 규칙: {family}{N}-... → {family}{N+1}-...
     * 예외: e2 시리즈는 다음 세대 없음
     */
    private static final Set<String> GCP_NO_NEXT_GEN = Set.of("e2");

    public String findModernizeSpec(GcpVmRightSizeDto vm) {
        String nsId            = vm.getTbbNsId();
        String currentSpecName = vm.getCurrentSpecName();
        if (nsId == null || currentSpecName == null) return null;

        String nextGenSpecName = resolveNextGenSpecName(currentSpecName);
        if (nextGenSpecName == null) {
            log.info("Modernize 다음 세대 없음 - vmId: {}, spec: {}", vm.getVmId(), currentSpecName);
            return null;
        }

        String found = tryGetSpec(nsId, nextGenSpecName);
        log.info("Modernize 추천 - vmId: {}, {} → {}",
                vm.getVmId(), currentSpecName, found != null ? found : "없음");
        if (found != null) vm.setRecommendSpecName(found);
        return found;
    }

    private String resolveNextGenSpecName(String currentSpecName) {
        // n1-standard-4 → n2-standard-4
        // c2-standard-8 → c3-standard-8
        // e2-medium     → null (예외)
        Pattern p = Pattern.compile("^([a-z]+)(\\d+)(-.*)?$");
        Matcher m = p.matcher(currentSpecName.toLowerCase());
        if (!m.matches()) return null;

        String family = m.group(1);
        int    gen    = Integer.parseInt(m.group(2));
        String rest   = m.group(3) != null ? m.group(3) : "";

        if (GCP_NO_NEXT_GEN.contains(family + gen)) return null;

        return family + (gen + 1) + rest;
    }

    private String tryGetSpec(String nsId, String specName) {
        JsonNode body = getSpec(nsId, specName);
        return body == null ? null : body.path("cspSpecName").asText(null);
    }
}
