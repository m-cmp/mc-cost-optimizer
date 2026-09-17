package com.mcmp.costselector.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.mcmp.costselector.unused.model.UnusedResourceStatusModel;
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

            // specId(예: azure+koreacentral+standard_b1s)는 리전까지 특정되므로 우선 사용,
            // 없거나 조회 실패 시 cspSpecName(예: Standard_B1s)으로 재시도
            String specId      = body.path("specId").asText(null);
            String cspSpecName = body.path("cspSpecName").asText(null);
            String regionName  = body.path("region").path("Region").asText(null);
            rscStatus.setTbbRegionName(regionName);

            boolean filled = specId != null && !specId.isEmpty()
                    && fillSpecDetail(rscStatus, nsId, specId);
            if (!filled && cspSpecName != null && !cspSpecName.isEmpty() && !cspSpecName.equals(specId)) {
                fillSpecDetail(rscStatus, nsId, cspSpecName);
            }
        } catch (Exception e) {
            log.warn("TBB VM 조회 실패 - url: {}, cause: {}", url, e.getMessage());
        }
    }

    private boolean fillSpecDetail(UnusedResourceStatusModel rscStatus, String nsId, String specKey) {
        JsonNode body = getSpec(nsId, specKey);
        if (body == null) {
            log.warn("TBB spec 상세 조회 실패 - specKey: {}, ns: system/{}", specKey, nsId);
            return false;
        }

        rscStatus.setCurrentSpecName(body.path("cspSpecName").asText(null));
        rscStatus.setCurrentVcpu(body.path("vCPU").asInt(0));
        rscStatus.setCurrentMemGiB(body.path("memoryGiB").asDouble(0));
        double rawCost = body.path("costPerHour").asDouble(0);
        rscStatus.setCurrentCostPerHour(rawCost > 0 ? rawCost : null);
        log.info("현재 스펙 조회 - resourceId: {}, spec: {}, vCPU: {}, mem: {}GiB, cost: ${}/h",
                rscStatus.getResource_id(), rscStatus.getCurrentSpecName(),
                rscStatus.getCurrentVcpu(), rscStatus.getCurrentMemGiB(),
                rscStatus.getCurrentCostPerHour());
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
        JsonNode body = getSpec(nsId, specName);
        return body == null ? null : body.path("cspSpecName").asText(null);
    }
}
