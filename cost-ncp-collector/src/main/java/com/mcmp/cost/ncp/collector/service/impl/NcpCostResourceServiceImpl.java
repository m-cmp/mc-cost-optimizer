package com.mcmp.cost.ncp.collector.service.impl;

import com.mcmp.cost.ncp.collector.client.NksClusterClient;
import com.mcmp.cost.ncp.collector.config.RestClientConfig;
import com.mcmp.cost.ncp.collector.constants.NcpApiUrl;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCost;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCostList;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCostListWrapper;
import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.dto.NksCluster;
import com.mcmp.cost.ncp.collector.entity.NcpCostResourceMonth;
import com.mcmp.cost.ncp.collector.mapper.NcpCostResourceDailyMapper;
import com.mcmp.cost.ncp.collector.properties.NcpResourceCollectProperties;
import com.mcmp.cost.ncp.collector.service.NcpCostResourceService;
import com.mcmp.cost.ncp.collector.service.mapping.NcpResourceCostMapper;
import com.mcmp.cost.ncp.collector.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

@Slf4j
@Service
@RequiredArgsConstructor
public class NcpCostResourceServiceImpl implements NcpCostResourceService {

    static final int PAGE_SIZE = 1000;

    private final RestClientConfig restClientConfig;
    private final NcpResourceCollectProperties properties;
    private final NksClusterClient nksClusterClient;
    private final NcpCostResourceDailyMapper mapper;

    @Override
    public List<NcpCostResourceMonth> getCostByResource(NcpApiCredentialDto ncpApiCredentialDto) {
        RestClient restClient = restClientConfig.createRestClientWithKey(
                ncpApiCredentialDto.getIamAccessKey(),
                ncpApiCredentialDto.getIamSecretKey());

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String yearMonth = DateUtils.getYearMonth(today);          // 1일이면 전월 (VM 경로와 동일)

        List<NcpCostResourceMonth> out = new ArrayList<>();
        Map<String, Map<String, NksCluster>> nksCache = new HashMap<>();   // regionCode → (instanceNo → cluster), lazy

        for (NcpResourceCollectProperties.TypeSpec spec : properties.getTypes()) {
            List<String[]> filters = buildFilters(spec);
            if (filters.isEmpty()) {
                log.warn("[{}] demandType 코드가 설정되지 않아 수집을 건너뜁니다. (env NCP_*_DEMAND_TYPE_CODES — Phase 0 프로브로 코드값 확인 필요)", spec.getResourceType());
                continue;
            }
            for (String[] f : filters) {
                String param = f[0];
                String code = f[1];
                List<ContractDemandCost> rows = collectPages(pageNo -> fetchPage(restClient, yearMonth, param, code, pageNo));
                if (rows.isEmpty()) {
                    log.info("[{}] {}={} 응답 0건 (demandMonth={})", spec.getResourceType(), param, code, yearMonth);
                    continue;
                }
                Date firstWriteDate = rows.getFirst().getWriteDate();
                if (isStale(firstWriteDate, today)) {
                    log.info("[{}] {}={} writeDate {} 가 2일 이상 오래되어 수집을 종료합니다. (today={})", spec.getResourceType(), param, code, firstWriteDate, today);
                    continue;
                }
                for (ContractDemandCost c : rows) {
                    if (c.getContract() == null && !"OBJECT_STORAGE".equals(spec.getResourceType())) {
                        log.warn("[{}] contract 가 없는 행은 건너뜁니다. memberNo={}, demandMonth={}", spec.getResourceType(), c.getMemberNo(), c.getDemandMonth());
                        continue;
                    }
                    Map<String, NksCluster> nks = spec.isResolveNksUuid()
                            ? nksCache.computeIfAbsent(regionOf(c), r -> nksClusterClient.listByInstanceNo(restClient, r))
                            : Map.of();
                    try {
                        NcpCostResourceMonth entity = NcpResourceCostMapper.toEntity(c, spec.getResourceType(), nks,
                                instNo -> spec.isResolveNksUuid() ? mapper.selectResolvedResourceId(c.getMemberNo(), instNo) : null);
                        log.debug("수집된 NcpCostResourceMonth: {}", entity);
                        out.add(entity);
                    } catch (IllegalArgumentException e) {
                        log.warn("[{}] 매핑 불가 행 건너뜀: {}", spec.getResourceType(), e.getMessage());
                    }
                }
            }
        }

        // UNIQUE 키 + INSERT IGNORE 로 재실행 멱등 (같은 날 스냅샷은 1회만 적재)
        int inserted = 0;
        for (NcpCostResourceMonth e : out) {
            inserted += mapper.insertResourceMonthIgnore(e);
        }
        log.info("NCP 자원(K8S/Object Storage) 비용 데이터 {}건 중 {}건 저장 완료 (중복 제외)", out.size(), inserted);
        return out;
    }

    /** 상세 코드가 설정되어 있으면 demandTypeDetailCode 로, 아니면 demandTypeCode 로 필터. 빈 값은 제외. */
    static List<String[]> buildFilters(NcpResourceCollectProperties.TypeSpec spec) {
        List<String[]> result = new ArrayList<>();
        List<String> details = spec.getDemandTypeDetailCodes() == null ? List.of() : spec.getDemandTypeDetailCodes();
        List<String> codes = spec.getDemandTypeCodes() == null ? List.of() : spec.getDemandTypeCodes();
        boolean hasDetail = details.stream().anyMatch(s -> s != null && !s.isBlank());
        if (hasDetail) {
            details.stream().filter(s -> s != null && !s.isBlank()).forEach(s -> result.add(new String[]{"demandTypeDetailCode", s.trim()}));
        } else {
            codes.stream().filter(s -> s != null && !s.isBlank()).forEach(s -> result.add(new String[]{"demandTypeCode", s.trim()}));
        }
        return result;
    }

    /** NCP 발행 지연 허용: writeDate 가 오늘 또는 어제면 수집, 이틀 이상 오래되면 stale (VM 경로와 동일 정책). */
    static boolean isStale(Date writeDate, LocalDate today) {
        if (writeDate == null) return true;
        LocalDate given = writeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return given.isBefore(today.minusDays(1));
    }

    /** pageNo 1부터 totalRows 에 도달하거나 빈 페이지가 올 때까지 모은다. 기존 VM 경로는 페이징이 없어 1,000건 초과 시 누락되던 문제를 피한다. */
    static List<ContractDemandCost> collectPages(IntFunction<ContractDemandCostList> pageLoader) {
        List<ContractDemandCost> all = new ArrayList<>();
        int pageNo = 1;
        while (true) {
            ContractDemandCostList page = pageLoader.apply(pageNo);
            if (page == null || page.getContractDemandCostList() == null || page.getContractDemandCostList().isEmpty()) break;
            all.addAll(page.getContractDemandCostList());
            Integer total = page.getTotalRows();
            if (total == null || all.size() >= total || page.getContractDemandCostList().size() < PAGE_SIZE) break;
            pageNo++;
        }
        return all;
    }

    private ContractDemandCostList fetchPage(RestClient restClient, String yearMonth, String param, String code, int pageNo) {
        String url = NcpApiUrl.CONTRACT_DEMAND_COST_LIST_URL
                + "?startMonth=" + yearMonth
                + "&endMonth=" + yearMonth
                + "&" + param + "=" + code
                + "&pageNo=" + pageNo
                + "&pageSize=" + PAGE_SIZE
                + "&responseFormatType=json";
        log.debug("NCP API 요청 URL: {}", url);
        ContractDemandCostListWrapper wrapper = restClient.get().uri(url).retrieve().body(ContractDemandCostListWrapper.class);
        return wrapper == null ? null : wrapper.getGetContractDemandCostListResponse();
    }

    private static String regionOf(ContractDemandCost c) {
        if (c.getRegionCode() != null && !c.getRegionCode().isBlank()) return c.getRegionCode();
        if (c.getContract() != null && c.getContract().getRegionCode() != null && !c.getContract().getRegionCode().isBlank()) return c.getContract().getRegionCode();
        return "KR";
    }
}
