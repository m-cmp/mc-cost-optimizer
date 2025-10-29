package com.mcmp.cost.ncp.collector.service.impl;

import com.mcmp.cost.ncp.collector.config.RestClientConfig;
import com.mcmp.cost.ncp.collector.constants.NcpApiUrl;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCost;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCostList;
import com.mcmp.cost.ncp.collector.dto.ContractDemandCostListWrapper;
import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.dto.ProductDemandCost;
import com.mcmp.cost.ncp.collector.dto.ProductDemandCostList;
import com.mcmp.cost.ncp.collector.dto.ProductDemandCostListWrapper;
import com.mcmp.cost.ncp.collector.dto.ServerInstance;
import com.mcmp.cost.ncp.collector.dto.ServerInstanceDetailWrapper;
import com.mcmp.cost.ncp.collector.dto.ServerInstanceList;
import com.mcmp.cost.ncp.collector.entity.NcpCostServiceMonth;
import com.mcmp.cost.ncp.collector.entity.NcpCostVmMonth;
import com.mcmp.cost.ncp.collector.repository.NcpCostVmMonthRepository;
import com.mcmp.cost.ncp.collector.service.NcpCostMonthService;
import com.mcmp.cost.ncp.collector.utils.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.NoSuchElementException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NcpCostMonthServiceImpl implements NcpCostMonthService {

    private final RestClientConfig restClientConfig;
    private final NcpCostVmMonthRepository ncpCostVmMonthRepository;

    public List<NcpCostServiceMonth> getCostByService(NcpApiCredentialDto ncpApiCredentialDto) {
        RestClient restClient = restClientConfig.createRestClientWithKey(
                ncpApiCredentialDto.getIamAccessKey(),
                ncpApiCredentialDto.getIamSecretKey());

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String yearMonth = DateUtils.getYearMonth(today);

        // 설정 기간에 따라 서비스별 청구 비용 목록을 조회 (월단위),
        String url = NcpApiUrl.PRODUCT_DEMAND_COST_LIST_URL
                + "?startMonth=" + yearMonth
                + "&endMonth=" + yearMonth
                + "&responseFormatType=json";
        log.debug("url: {}", url);

        ProductDemandCostListWrapper response = restClient.get()
                .uri(url)
                .retrieve()
                .body(ProductDemandCostListWrapper.class);
        ProductDemandCostList productDemandCostList = Objects.requireNonNull(response).getGetProductDemandCostListResponse();

        List<NcpCostServiceMonth> ncpCostServiceMonthList = new ArrayList<>();
        // 매일 청구 비용이 NCP에서 한국시간 8시쯤 제공한다.
        // 해당 시간에 대한 정보는 productDemandCostList 객체 내 WriteDate를 통해서 알 수 있다.
        // 수집할때, 해당 필드가 당일 날짜인지를 확인하여, 데이터를 적재할지를 판단한다.
        // 1일 인 경우, 전달 청구 금액이 전달되는 것으로 확인된다.
        ProductDemandCost todayProductDemandCost = productDemandCostList.getProductDemandCostList().getFirst();
        LocalDate givenDate = todayProductDemandCost.getWriteDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        log.info("getCostByService - today: {}, givenDate: {}", today, givenDate);

        if (!givenDate.equals(today)) {
            log.info("{} 비용 데이터가 NCP에서 제공되지 않아 수집을 종료합니다.", givenDate);
            return ncpCostServiceMonthList;
        }

        for (ProductDemandCost productDemandCost : productDemandCostList.getProductDemandCostList()) {
            NcpCostServiceMonth ncpCostServiceMonth = NcpCostServiceMonth.builder()
                    .memberNo(productDemandCost.getMemberNo())
                    .demandAmount(productDemandCost.getDemandAmount())
                    .demandMonth(productDemandCost.getDemandMonth())
                    .useAmount(productDemandCost.getUseAmount())
                    .productDemandType(productDemandCost.getProductDemandType().getCodeName())
                    .writeDate(productDemandCost.getWriteDate())
                    .payCurrency(productDemandCost.getPayCurrency().getCode())
                    .build();
            log.debug("NcpCostServiceMonth: {}", ncpCostServiceMonth.toString());
            ncpCostServiceMonthList.add(ncpCostServiceMonth);
        }

        return ncpCostServiceMonthList;
    }

//    @Override
//    public List<NcpCostVmMonth> getCostByVm(NcpApiCredentialDto ncpApiCredentialDto) {
//        RestClient restClient = restClientConfig.createRestClientWithKey(
//                ncpApiCredentialDto.getIamAccessKey(),
//                ncpApiCredentialDto.getIamSecretKey());
//
//        LocalDate today = LocalDate.now(ZoneId.systemDefault());
//
//        String yearMonth = DateUtils.getYearMonth(today);
//        // 설정 기간에 따라 VM별 청구 비용을 조회 (월단위),
//        String url = NcpApiUrl.CONTRACT_DEMAND_COST_LIST_URL
//                + "?startMonth=" + yearMonth
//                + "&endMonth=" + yearMonth
//                + "&demandTypeDetailCode=" + "VSVR" // VM만 조회하는 코드.
//                + "&responseFormatType=json";
//        log.debug("url: {}", url);
//
//        ContractDemandCostListWrapper response = restClient.get()
//                .uri(url)
//                .retrieve()
//                .body(ContractDemandCostListWrapper.class);
//        ContractDemandCostList contractDemandCostList = Objects.requireNonNull(response).getGetContractDemandCostListResponse();
//
//        // 매일 청구 비용이 NCP에서 한국시간 8시쯤 제공한다.
//        // 해당 시간에 대한 정보는 contractDemandCostList 객체 내 WriteDate를 통해서 알 수 있다.
//        // 수집할때, 해당 필드가 당일 날짜인지를 확인하여, 데이터를 적재할지를 판단한다.
//        // 1일 인 경우, 전달 청구 금액이 전달되는 것으로 확인된다.
//        ContractDemandCost todayContractDemandCost = contractDemandCostList.getContractDemandCostList().getFirst();
//        LocalDate givenDate = todayContractDemandCost.getWriteDate()
//                .toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate();
//
//        log.info("getCostByVm - today: {}, givenDate: {}", today, givenDate);
//
//        List<NcpCostVmMonth> ncpCostVmMonthList = new ArrayList<>();
//        if (!givenDate.equals(today)) {
//            log.info("{} VM 비용 데이터가 NCP에서 제공되지 않아 수집을 종료합니다.", givenDate);
//            return ncpCostVmMonthList;
//        }
//
//        for (ContractDemandCost contractDemandCost : contractDemandCostList.getContractDemandCostList()) {
//            // Instance 정보 조회.(serverSpecCode을 조회하기 위한 용도)
//            String instanceDetailUrl = NcpApiUrl.SERVER_DETAIL_URL
//                    + "?serverInstanceNo=" + contractDemandCost.getContract().getContractProductList().getFirst().getInstanceNo()
//                    + "&responseFormatType=json";
//            ServerInstanceDetailWrapper instanceResponse = restClient.get()
//                    .uri(instanceDetailUrl)
//                    .retrieve()
//                    .body(ServerInstanceDetailWrapper.class);
//            ServerInstanceList serverInstanceList = Objects.requireNonNull(instanceResponse)
//                    .getGetServerInstanceDetailResponse();
//            ServerInstance serverInstance = serverInstanceList.getServerInstanceList().getFirst();
//
//            // DB Insert.
//            NcpCostVmMonth ncpCostVmMonth = NcpCostVmMonth.builder()
//                    .memberNo(contractDemandCost.getMemberNo())
//                    .demandMonth(contractDemandCost.getDemandMonth())
//                    .regionCode(contractDemandCost.getRegionCode())
//                    .serverSpecCode(serverInstance.getServerSpecCode())
//                    .instanceNo(serverInstance.getServerInstanceNo())
//                    .instanceName(serverInstance.getServerName())
//                    .usageUnitCode(contractDemandCost.getUsageUnit().getCode())
//                    .usageUnitName(contractDemandCost.getUsageUnit().getCodeName())
//                    .productPrice(contractDemandCost.getProductPrice())
//                    .unitUsageQuantity(contractDemandCost.getUnitUsageQuantity())
//                    .totalUnitUsageQuantity(contractDemandCost.getTotalUnitUsageQuantity())
//                    .useAmount(contractDemandCost.getUseAmount())
//                    .demandAmount(contractDemandCost.getDemandAmount())
//                    .writeDate(contractDemandCost.getWriteDate())
//                    .payCurrency(contractDemandCost.getPayCurrency().getCode())
//                    .build();
//            log.debug("NcpCostVmMonth: {}", ncpCostVmMonth.toString());
//            ncpCostVmMonthList.add(ncpCostVmMonth);
//        }
//        // API로 조회한 원천 데이터를 그대로 수집. 일일 데이터는 배치를 통해서 정제하여 다른 테이블에 수집한다.
//        ncpCostVmMonthRepository.saveAll(ncpCostVmMonthList);
//        return ncpCostVmMonthList;
//    }

    @Override
    public List<NcpCostVmMonth> getCostByVm(NcpApiCredentialDto ncpApiCredentialDto) {
        RestClient restClient = restClientConfig.createRestClientWithKey(
                ncpApiCredentialDto.getIamAccessKey(),
                ncpApiCredentialDto.getIamSecretKey());

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        String yearMonth = DateUtils.getYearMonth(today);

        String url = NcpApiUrl.CONTRACT_DEMAND_COST_LIST_URL
                + "?startMonth=" + yearMonth
                + "&endMonth=" + yearMonth
                + "&demandTypeDetailCode=VSVR"
                + "&responseFormatType=json";
        log.debug("NCP API 요청 URL: {}", url);

        ContractDemandCostListWrapper response = restClient.get()
                .uri(url)
                .retrieve()
                .body(ContractDemandCostListWrapper.class);

        ContractDemandCostList contractDemandCostList = Objects.requireNonNull(response)
                .getGetContractDemandCostListResponse();

        List<ContractDemandCost> demandCostList = contractDemandCostList.getContractDemandCostList();
        if (demandCostList == null) {
            log.error("응답이 NULL입니다. Wrapper: {}", response);
            return new ArrayList<>();
        }

        log.info("응답 ContractDemandCost 개수: {}", demandCostList.size());

        if (!demandCostList.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String sampleJson = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(demandCostList.get(0));
                log.debug("응답 샘플(첫 번째 ContractDemandCost):\n{}", sampleJson);
            } catch (Exception e) {
                log.warn("응답 샘플 직렬화 중 오류 발생", e);
            }
        }

        // 첫 번째 요소 접근
        LocalDate givenDate;
        try {
            ContractDemandCost todayContractDemandCost = demandCostList.getFirst();
            givenDate = todayContractDemandCost.getWriteDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            log.info("getCostByVm - today: {}, givenDate: {}", today, givenDate);
        } catch (NoSuchElementException e) {
            log.error("getFirst() 실패 — demandCostList가 비어 있습니다. URL: {}", url, e);
            return new ArrayList<>();
        }

        List<NcpCostVmMonth> ncpCostVmMonthList = new ArrayList<>();
        if (!givenDate.equals(today)) {
            log.info("{} VM 비용 데이터가 아직 제공되지 않았습니다. 수집 종료.", givenDate);
            return ncpCostVmMonthList;
        }

        for (ContractDemandCost contractDemandCost : demandCostList) {
            if (contractDemandCost.getContract() == null ||
                    contractDemandCost.getContract().getContractProductList() == null ||
                    contractDemandCost.getContract().getContractProductList().isEmpty()) {
                log.warn("contractProductList가 비어 있습니다. contractDemandCost: {}", contractDemandCost);
                continue;
            }

            // ContractProductList 첫 번째 접근 로그
            log.debug("contractProductList size: {}",
                    contractDemandCost.getContract().getContractProductList().size());

            String instanceNo = null;
            try {
                instanceNo = contractDemandCost.getContract()
                        .getContractProductList()
                        .getFirst()
                        .getInstanceNo();
                log.debug("InstanceNo 추출 성공: {}", instanceNo);
            } catch (NoSuchElementException e) {
                log.error("contractProductList.getFirst() 실패: {}", contractDemandCost, e);
                continue;
            }

            // 서버 상세조회 URL 로그
            String instanceDetailUrl = NcpApiUrl.SERVER_DETAIL_URL
                    + "?regionCode=" + contractDemandCost.getRegionCode()
                    + "&serverInstanceNo=" + instanceNo
                    + "&responseFormatType=json";
            log.debug("서버 상세 조회 URL: {}", instanceDetailUrl);

            ServerInstanceDetailWrapper instanceResponse = restClient.get()
                    .uri(instanceDetailUrl)
                    .retrieve()
                    .body(ServerInstanceDetailWrapper.class);

            ServerInstanceList serverInstanceList = Objects.requireNonNull(instanceResponse)
                    .getGetServerInstanceDetailResponse();

            if (serverInstanceList.getServerInstanceList() == null ||
                    serverInstanceList.getServerInstanceList().isEmpty()) {
                log.warn("서버 인스턴스 상세 응답이 비어 있습니다. instanceNo: {}", instanceNo);
                continue;
            }

            ServerInstance serverInstance;
            try {
                serverInstance = serverInstanceList.getServerInstanceList().getFirst();
                log.debug("서버 인스턴스 조회 성공: {}", serverInstance.getServerName());
            } catch (NoSuchElementException e) {
                log.error("serverInstanceList.getFirst() 실패. instanceNo: {}", instanceNo, e);
                continue;
            }

            // DB Insert.
            NcpCostVmMonth ncpCostVmMonth = NcpCostVmMonth.builder()
                    .memberNo(contractDemandCost.getMemberNo())
                    .demandMonth(contractDemandCost.getDemandMonth())
                    .regionCode(contractDemandCost.getRegionCode())
                    .serverSpecCode(serverInstance.getServerSpecCode())
                    .instanceNo(serverInstance.getServerInstanceNo())
                    .instanceName(serverInstance.getServerName())
                    .usageUnitCode(contractDemandCost.getUsageUnit().getCode())
                    .usageUnitName(contractDemandCost.getUsageUnit().getCodeName())
                    .productPrice(contractDemandCost.getProductPrice())
                    .unitUsageQuantity(contractDemandCost.getUnitUsageQuantity())
                    .totalUnitUsageQuantity(contractDemandCost.getTotalUnitUsageQuantity())
                    .useAmount(contractDemandCost.getUseAmount())
                    .demandAmount(contractDemandCost.getDemandAmount())
                    .writeDate(contractDemandCost.getWriteDate())
                    .payCurrency(contractDemandCost.getPayCurrency().getCode())
                    .build();

            log.debug("수집된 NcpCostVmMonth: {}", ncpCostVmMonth);
            ncpCostVmMonthList.add(ncpCostVmMonth);
        }

        ncpCostVmMonthRepository.saveAll(ncpCostVmMonthList);
        log.info("NCP VM 비용 데이터 {}건 저장 완료", ncpCostVmMonthList.size());
        return ncpCostVmMonthList;
    }

}
