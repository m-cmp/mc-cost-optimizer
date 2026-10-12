package com.mcmp.costbe.tumblebugMeta.service;

import com.mcmp.costbe.tumblebugMeta.dao.TBBDao;
import com.mcmp.costbe.tumblebugMeta.model.ResourcegroupMetaModel;
import com.mcmp.costbe.tumblebugMeta.model.k8s.K8sClusterItemModel;
import com.mcmp.costbe.tumblebugMeta.model.k8s.K8sClusterListModel;
import com.mcmp.costbe.tumblebugMeta.model.k8s.K8sNodeGroupModel;
import com.mcmp.costbe.tumblebugMeta.model.k8s.K8sNodeModel;
import com.mcmp.costbe.tumblebugMeta.model.mci.TBBMCIItemModel;
import com.mcmp.costbe.tumblebugMeta.model.objectStorage.ObjectStorageItemModel;
import com.mcmp.costbe.tumblebugMeta.model.objectStorage.ObjectStorageListModel;
import com.mcmp.costbe.tumblebugMeta.model.mci.TBBMCIModel;
import com.mcmp.costbe.tumblebugMeta.model.mci.TbInfraNodeModel;
import com.mcmp.costbe.tumblebugMeta.model.mci.TbInfraNodeSpecModel;
import com.mcmp.costbe.tumblebugMeta.model.mci.TbVmInfoModel;
import com.mcmp.costbe.tumblebugMeta.model.ns.TBBNSItemModel;
import com.mcmp.costbe.tumblebugMeta.model.ns.TBBNSModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class VMMetaService {

    @Value("${tumblebug.url}")
    public String tumblebugUrl;

    @Value("${tumblebug.username}")
    public String tumblebugUserNM;

    @Value("${tumblebug.password}")
    public String tumblebugPW;

    @Autowired
    private TBBDao tbbDao;

    // Retry tuning for the 2 req/sec rate limit on Tumblebug's node-spec endpoint.
    /** servicegroup_meta 의 NOT NULL 컬럼(vm_id, mci_id) 기본값. 값이 없는 행(K8s 의 MCI 등)에 사용 */
    private static final String META_UNDEFINED = "undefined";

    private static final int TBB_SPEC_MAX_RETRIES = 4;
    private static final long TBB_SPEC_BACKOFF_BASE_MS = 300L;
    private static final long TBB_SPEC_BACKOFF_JITTER_MS = 200L;

    /**
     * CSP별 계정 ID 추출
     * AWS: NetworkInterfaces의 OwnerId (12자리 숫자)
     * NCP: additionalDetails에서 memberNo 추출 또는 별도 필드
     * Azure: resourceId에서 subscription_id 파싱
     *
     * @param vminfo VM 상세 정보
     * @param cspType CSP 타입 (AWS, NCP, AZURE 등)
     * @return CSP 계정 ID, 없으면 null
     */
    private String extractCspAccountId(TbVmInfoModel vminfo, String cspType) {
        if(vminfo == null) {
            return null;
        }

        String accountId = null;

        switch(cspType.toUpperCase()) {
            case "AWS":
                accountId = extractAwsAccountId(vminfo.getAddtionalDetails());
                break;
            case "NCP":
                accountId = extractNcpAccountId(vminfo.getAddtionalDetails());
                break;
            case "AZURE":
                accountId = extractAzureAccountId(vminfo.getCspResourceId(), vminfo.getAddtionalDetails());
                break;
            case "GCP":
                accountId = extractGcpAccountId(vminfo.getAddtionalDetails());
                break;
            default:
                log.warn("Unsupported CSP type: {}", cspType);
        }

        if(accountId != null) {
            log.debug("Extracted {} Account ID: {}", cspType, accountId);
        } else {
            log.warn("Could not extract {} Account ID", cspType);
        }

        return accountId;
    }

    /**
     * AWS 계정 ID (OwnerId) 추출
     * NetworkInterfaces의 OwnerId에서 12자리 숫자 추출
     */
    private String extractAwsAccountId(List<Map<String, Object>> additionalDetails) {
        if(additionalDetails == null || additionalDetails.isEmpty()) {
            return null;
        }

        Pattern ownerIdPattern = Pattern.compile("OwnerId:(\\d{12})");

        for(Map<String, Object> detail : additionalDetails) {
            String key = (String) detail.get("key");
            String value = (String) detail.get("value");

            if("NetworkInterfaces".equals(key) && value != null) {
                Matcher matcher = ownerIdPattern.matcher(value);
                if(matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return null;
    }

    /**
     * NCP 계정 ID (memberNo) 추출
     * additionalDetails에서 memberNo 또는 accountNo 패턴 찾기
     */
    private String extractNcpAccountId(List<Map<String, Object>> additionalDetails) {
        if(additionalDetails == null || additionalDetails.isEmpty()) {
            return null;
        }

        // memberNo 또는 accountNo 패턴 찾기
        Pattern memberNoPattern = Pattern.compile("(?:memberNo|accountNo|member_no):(\\d+)", Pattern.CASE_INSENSITIVE);

        for(Map<String, Object> detail : additionalDetails) {
            String key = (String) detail.get("key");
            String value = (String) detail.get("value");

            if(value != null) {
                Matcher matcher = memberNoPattern.matcher(value);
                if(matcher.find()) {
                    return matcher.group(1);
                }
            }

            // key 자체가 memberNo나 accountNo인 경우
            if(key != null && (key.equalsIgnoreCase("memberNo") ||
                    key.equalsIgnoreCase("accountNo") ||
                    key.equalsIgnoreCase("member_no"))) {
                return (String) detail.get("value");
            }
        }
        return null;
    }

    /**
     * Azure 계정 ID (subscription_id) 추출
     * 1. resourceId에서 파싱: /subscriptions/{subscription_id}/...
     * 2. additionalDetails에서 찾기
     */
    private String extractAzureAccountId(String resourceId, List<Map<String, Object>> additionalDetails) {
        // 1. resourceId에서 subscription_id 파싱 (우선순위 높음)
        if(resourceId != null && resourceId.contains("/subscriptions/")) {
            Pattern subscriptionPattern = Pattern.compile("/subscriptions/([a-f0-9-]{36})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = subscriptionPattern.matcher(resourceId);
            if(matcher.find()) {
                return matcher.group(1);
            }
        }

        // 2. additionalDetails에서 찾기
        if(additionalDetails != null && !additionalDetails.isEmpty()) {
            Pattern subIdPattern = Pattern.compile("(?:subscriptionId|subscription_id)\\s*[:\\=]\\s*([a-f0-9-]{36})", Pattern.CASE_INSENSITIVE);

            for(Map<String, Object> detail : additionalDetails) {
                String key = (String) detail.get("key");
                String value = (String) detail.get("value");

                if(value != null) {
                    Matcher matcher = subIdPattern.matcher(value);
                    if(matcher.find()) {
                        return matcher.group(1);
                    }
                }

                // key 자체가 subscriptionId인 경우
                if(key != null && (key.equalsIgnoreCase("subscriptionId") ||
                        key.equalsIgnoreCase("subscription_id"))) {
                    return (String) detail.get("value");
                }
            }
        }
        return null;
    }

    /**
     * GCP 계정 ID (project_id) 추출
     * additionalDetails에서 projectId 또는 projectNumber 찾기
     */
    private String extractGcpAccountId(List<Map<String, Object>> additionalDetails) {
        if(additionalDetails == null || additionalDetails.isEmpty()) {
            return null;
        }

        Pattern projectPattern = Pattern.compile("(?:projectId|project_id|projectNumber)\\s*[:\\=]\\s*([\\w-]+)", Pattern.CASE_INSENSITIVE);

        for(Map<String, Object> detail : additionalDetails) {
            String key = (String) detail.get("key");
            String value = (String) detail.get("value");

            if(value != null) {
                Matcher matcher = projectPattern.matcher(value);
                if(matcher.find()) {
                    return matcher.group(1);
                }
            }

            // key 자체가 projectId인 경우
            if(key != null && (key.equalsIgnoreCase("projectId") ||
                    key.equalsIgnoreCase("project_id") ||
                    key.equalsIgnoreCase("projectNumber"))) {
                return (String) detail.get("value");
            }
        }
        return null;
    }


    public List<TBBNSItemModel> getTbbNS(){

        String apiUrl = String.format("%s/ns", tumblebugUrl);
        RestTemplate restTemplate = new RestTemplate();

        String auth = tumblebugUserNM + ":" + tumblebugPW;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", authHeader);
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        try{
            ResponseEntity<TBBNSModel> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, TBBNSModel.class);
            TBBNSModel response = responseEntity.getBody();

            if(response.getNs() != null && !response.getNs().isEmpty()){
                return response.getNs();
            }else{
                log.warn("TUMBLEBUG META - NS IS EMPTY => response : {}", response);
                return new ArrayList<>();
            }
        } catch (HttpClientErrorException | HttpServerErrorException clientError) {
            HttpStatus statusCode = clientError.getStatusCode();
            log.error("FAIL TO GET TUMBLEBUG META - NS : " + statusCode);
            throw new RuntimeException();
        } catch (Exception e){
            log.error("FAIL TO GET TUMBLEBUG META - NS : " + e.getMessage());
            throw new RuntimeException();
        }
    }

    public List<TBBMCIItemModel> getTBBMCI(TBBNSItemModel item){

        if(item != null){
            String apiUrl = String.format("%s/ns/%s/infra", tumblebugUrl, item.getId());
            RestTemplate restTemplate = new RestTemplate();

            String auth = tumblebugUserNM + ":" + tumblebugPW;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", authHeader);
            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

            try{
                ResponseEntity<TBBMCIModel> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, TBBMCIModel.class);
                TBBMCIModel response = responseEntity.getBody();

                if(response.getMci() != null && !response.getMci().isEmpty()){
                    return response.getMci();
                }else{
                    log.warn("TUMBLEBUG META - MCI => MCI IS EMPTY => ns : {}, response : {}", item.getId(), response);
                    return new ArrayList<>();
                }
            } catch (HttpClientErrorException | HttpServerErrorException clientError) {
                HttpStatus statusCode = clientError.getStatusCode();
                log.error("FAIL TO GET TUMBLEBUG META - MCI => NS ID : {}, error code : {}", item.getId(), statusCode);
                throw new RuntimeException();
            } catch (Exception e){
                log.error("FAIL TO GET TUMBLEBUG META - MCI => NS ID : {}, error : {}", item.getId(), e.getMessage());
                throw new RuntimeException();
            }

        } else {
            log.error("[ERROR] : GET TUMBLEBUG META - MCI => NS IS EMPTY");
            return new ArrayList<>();
        }
    }

    /**
     * GET /ns/{nsId}/k8sCluster — Tumblebug이 관리하는 K8s 클러스터 목록.
     * Object Storage와 마찬가지로 실패를 치명적으로 보지 않는다: 404(미지원 Tumblebug 버전)·타임아웃·5xx 시
     * 빈 목록을 반환해, 해당 ns의 Object Storage와 이후 ns의 VM/K8s/Object Storage 메타 동기화가 계속되게 한다.
     */
    public List<K8sClusterItemModel> getTBBK8sClusters(TBBNSItemModel item){

        if(item != null){
            String apiUrl = String.format("%s/ns/%s/k8sCluster", tumblebugUrl, item.getId());
            RestTemplate restTemplate = new RestTemplate();

            String auth = tumblebugUserNM + ":" + tumblebugPW;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", authHeader);
            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

            try{
                ResponseEntity<K8sClusterListModel> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, K8sClusterListModel.class);
                K8sClusterListModel response = responseEntity.getBody();

                if(response != null && response.getClusters() != null && !response.getClusters().isEmpty()){
                    return response.getClusters();
                }else{
                    log.warn("TUMBLEBUG META - K8S => CLUSTERS IS EMPTY => ns : {}, response : {}", item.getId(), response);
                    return new ArrayList<>();
                }
            } catch (HttpClientErrorException.NotFound notFound) {
                log.warn("TUMBLEBUG META - K8S => endpoint not found (Tumblebug version may not support k8sCluster) => NS ID : {}", item.getId());
                return new ArrayList<>();
            } catch (HttpClientErrorException | HttpServerErrorException clientError) {
                HttpStatus statusCode = clientError.getStatusCode();
                log.error("FAIL TO GET TUMBLEBUG META - K8S => NS ID : {}, error code : {}", item.getId(), statusCode);
                return new ArrayList<>();
            } catch (Exception e){
                log.error("FAIL TO GET TUMBLEBUG META - K8S => NS ID : {}, error : {}", item.getId(), e.getMessage());
                return new ArrayList<>();
            }

        } else {
            log.error("[ERROR] : GET TUMBLEBUG META - K8S => NS IS EMPTY");
            return new ArrayList<>();
        }
    }

    /**
     * GET /ns/{nsId}/resources/objectStorage — Tumblebug이 관리하는 Object Storage(버킷) 목록.
     * K8s/VM 수집과 달리 실패를 치명적으로 보지 않는다: 404(미지원 Tumblebug 버전)·오류 시 빈 목록을 반환해
     * 기존 VM/K8s 메타 동기화가 중단되지 않게 한다.
     */
    public List<ObjectStorageItemModel> getTBBObjectStorages(TBBNSItemModel item){

        if(item == null){
            log.error("[ERROR] : GET TUMBLEBUG META - OBJECT STORAGE => NS IS EMPTY");
            return new ArrayList<>();
        }

        String apiUrl = String.format("%s/ns/%s/resources/objectStorage", tumblebugUrl, item.getId());
        RestTemplate restTemplate = new RestTemplate();

        String auth = tumblebugUserNM + ":" + tumblebugPW;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", authHeader);
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        try{
            ResponseEntity<ObjectStorageListModel> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, ObjectStorageListModel.class);
            ObjectStorageListModel response = responseEntity.getBody();

            if(response != null && response.getObjectStorages() != null && !response.getObjectStorages().isEmpty()){
                return response.getObjectStorages();
            }else{
                log.info("TUMBLEBUG META - OBJECT STORAGE => EMPTY => ns : {}", item.getId());
                return new ArrayList<>();
            }
        } catch (HttpClientErrorException.NotFound notFound) {
            log.warn("TUMBLEBUG META - OBJECT STORAGE => endpoint not found (Tumblebug version may not support object storage) => NS ID : {}", item.getId());
            return new ArrayList<>();
        } catch (HttpClientErrorException | HttpServerErrorException clientError) {
            log.error("FAIL TO GET TUMBLEBUG META - OBJECT STORAGE => NS ID : {}, error code : {}", item.getId(), clientError.getStatusCode());
            return new ArrayList<>();
        } catch (Exception e){
            log.error("FAIL TO GET TUMBLEBUG META - OBJECT STORAGE => NS ID : {}, error : {}", item.getId(), e.getMessage());
            return new ArrayList<>();
        }
    }

    public TbVmInfoModel getTBBVM(TBBNSItemModel item, TBBMCIItemModel mci, TbVmInfoModel vm){

        if(vm != null){
            String apiUrl = String.format("%s/ns/%s/infra/%s/node/%s", tumblebugUrl, item.getId(), mci.getId(), vm.getId());
            RestTemplate restTemplate = new RestTemplate();

            String auth = tumblebugUserNM + ":" + tumblebugPW;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", authHeader);
            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

            try{
                ResponseEntity<TbVmInfoModel> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, TbVmInfoModel.class);
                TbVmInfoModel response = responseEntity.getBody();

                if(response != null && !response.getId().isEmpty()){
                    return response;
                }else{
                    log.warn("TUMBLEBUG META - VM => VM IS EMPTY => ns : {}, mci : {}, response : {}", item.getId(), mci.getId(), response);
                    return null;
                }
            } catch (HttpClientErrorException | HttpServerErrorException clientError) {
                HttpStatus statusCode = clientError.getStatusCode();
                log.error("FAIL TO GET TUMBLEBUG META - VM => NS ID : {}, MCI ID : {}, VM ID : {}, error code : {}", item.getId(), mci.getId(), vm.getId(), statusCode);
                throw new RuntimeException();
            } catch (Exception e){
                log.error("FAIL TO GET TUMBLEBUG META - VM => NS ID : {}, MCI ID : {}, error : {}", item.getId(), mci.getId(), e.getMessage());
                throw new RuntimeException();
            }

        } else {
            log.error("[ERROR] : GET TUMBLEBUG META - MCI => NS IS EMPTY");
            return null;
        }
    }

    /**
     * Look up a single node's spec (cspSpecName/vCPU/memoryGiB) via
     * GET /ns/{nsId}/infra/{mciId}?nodeId={vmId}.
     * <p>
     * Used to enrich the recommend-tab instance list, where one HTTP call is
     * made per instance. A short timeout is used and any failure/timeout
     * returns null so a single slow/broken instance doesn't block the rest.
     */
    public TbInfraNodeSpecModel getTBBNodeSpec(String nsId, String mciId, String vmId) {

        if (nsId == null || mciId == null || vmId == null) {
            return null;
        }

        // Tumblebug의 GET /ns/{ns}/infra/{mci}는 nodeId 쿼리 파라미터를 지원하지 않아(무시됨) MCI 전체 node가
        // 반환되고 node[0]의 spec을 잡는 문제가 있었음. node 단건 조회 엔드포인트로 정확한 VM의 spec을 가져온다.
        String apiUrl = String.format("%s/ns/%s/infra/%s/node/%s", tumblebugUrl, nsId, mciId, vmId);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        String auth = tumblebugUserNM + ":" + tumblebugPW;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", authHeader);
        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        // Tumblebug throttles GET /ns/{ns}/infra/{mci}/node/{node} at 2 req/sec, so a burst of
        // per-VM lookups otherwise returns 429. Retry the 429s with exponential
        // backoff (+jitter) to let the token bucket refill before giving up.
        for (int attempt = 0; attempt <= TBB_SPEC_MAX_RETRIES; attempt++) {
            try {
                ResponseEntity<TbInfraNodeModel> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, TbInfraNodeModel.class);
                TbInfraNodeModel response = responseEntity.getBody();

                if (response != null && response.getSpec() != null && response.getSpec().getCspSpecName() != null) {
                    return response.getSpec();
                } else {
                    log.warn("TUMBLEBUG META - NODE SPEC => EMPTY => ns : {}, mci : {}, vm : {}, response : {}", nsId, mciId, vmId, response);
                    return null;
                }
            } catch (HttpClientErrorException.TooManyRequests e) {
                if (attempt == TBB_SPEC_MAX_RETRIES) {
                    log.warn("TUMBLEBUG META - NODE SPEC => RATE LIMITED after {} retries => ns : {}, mci : {}, vm : {}", TBB_SPEC_MAX_RETRIES, nsId, mciId, vmId);
                    return null;
                }
                long backoffMs = TBB_SPEC_BACKOFF_BASE_MS * (1L << attempt) + (long) (Math.random() * TBB_SPEC_BACKOFF_JITTER_MS);
                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            } catch (Exception e) {
                log.warn("FAIL TO GET TUMBLEBUG META - NODE SPEC => NS ID : {}, MCI ID : {}, VM ID : {}, error : {}", nsId, mciId, vmId, e.getMessage());
                return null;
            }
        }
        return null;
    }

    public void getTBBResourceMetaInfo() throws InterruptedException {
        List<TBBNSItemModel> nsList = getTbbNS();

        for(TBBNSItemModel ns : nsList){

            Thread.sleep(2000);

            // VM 처리
            List<TBBMCIItemModel> mciList = getTBBMCI(ns);

            for(TBBMCIItemModel mci : mciList){
                if("infra".equals(mci.getResourceType())){
                    try{

                        List<TbVmInfoModel> vmList = mci.getVm();
                        if(vmList != null && !vmList.isEmpty()){
                            for(TbVmInfoModel vm : vmList){
                                List<ResourcegroupMetaModel> resourcegroupMetaList = new ArrayList<>();
                                TbVmInfoModel vminfo =  getTBBVM(ns, mci, vm);

                                if(vminfo != null){
                                    String vmStatus;
                                    if(!vminfo.getStatus().isEmpty()){
                                        vmStatus = switch (vminfo.getStatus()){
                                            case "Running" -> "Y";
                                            case "Failed" -> "N";
                                            default -> "Y";
                                        };
                                    }else {
                                        vmStatus = "Y";
                                    }

                                    if(vminfo.getCspResourceId() != null){
                                        // CSP 타입 확인
                                        String cspType = vminfo.getConnectionConfig().getProviderName().toUpperCase();

                                        // CSP별 계정 ID 추출
                                        String cspAccountId = extractCspAccountId(vminfo, cspType);

                                        // 추출 실패 시 기본값 사용
                                        if(cspAccountId == null || cspAccountId.isEmpty()) {
                                            cspAccountId = "mcmpcostopti";
                                            log.warn("Could not extract {} Account ID, using default: {}", cspType, cspAccountId);
                                        }

                                        ResourcegroupMetaModel vmInfo = ResourcegroupMetaModel.builder()
                                                .cspType(cspType)
                                                .cspAccount(cspAccountId)
                                                .cspInstanceid(vminfo.getCspResourceId())
                                                .serviceCd(ns.getId())
                                                .serviceNm(ns.getName())
                                                .serviceType("VM")
                                                .workspaceCd("ws1")  // TODO: 추후 동적으로 변경 필요
                                                .vmId(vminfo.getId())
                                                .vmUid(vminfo.getUid())
                                                .vmNm(vminfo.getName())
                                                .mciId(mci.getId())
                                                .mciUid(mci.getUid())
                                                .mciNm(mci.getName())
                                                .instanceRunningStatus(vmStatus)
                                                .build();

                                        resourcegroupMetaList.add(vmInfo);
                                    }
                                }

                                if(resourcegroupMetaList.size() >= 1){
                                    tbbDao.insertTBBServicegroupMeta(resourcegroupMetaList);
                                }

                            }
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                        throw new RuntimeException();
                    }

                }
            }

            // K8s 클러스터 처리
            Thread.sleep(2000);
            List<K8sClusterItemModel> k8sList = getTBBK8sClusters(ns);

            if(k8sList != null && !k8sList.isEmpty()){
                List<ResourcegroupMetaModel> k8sMetaList = new ArrayList<>();

                for(K8sClusterItemModel cluster : k8sList){
                    if(cluster == null || cluster.getCspResourceId() == null) continue;

                    // 클러스터 1개 처리 실패가 같은 ns의 다른 클러스터 적재를 막지 않게 클러스터 단위로 격리
                    try{
                        // CSP 타입: 알 수 없으면 과금 데이터와 매칭될 수 없으므로 적재하지 않는다
                        if(cluster.getConnectionConfig() == null || cluster.getConnectionConfig().getProviderName() == null){
                            log.warn("K8s cluster {} in ns {} has no providerName, skipped", cluster.getId(), ns.getId());
                            continue;
                        }
                        String cspType = cluster.getConnectionConfig().getProviderName().toUpperCase();

                        // K8s 클러스터 상태 처리
                        String k8sStatus;
                        if(cluster.getStatus() != null && !cluster.getStatus().isEmpty()){
                            k8sStatus = switch (cluster.getStatus()){
                                case "Active" -> "Y";
                                case "Failed", "Deactive", "Inactive", "Error" -> "N";
                                default -> {
                                    log.warn("Unknown K8s cluster status: {} for cluster: {}, defaulting to Y",
                                        cluster.getStatus(), cluster.getId());
                                    yield "Y";
                                }
                            };
                        }else {
                            k8sStatus = "Y";
                        }

                        // 과금 데이터 매칭 키 정규화: AWS EKS는 CUR이 ARN을 쓰므로 keyValueList의 Arn으로 치환,
                        // 그 외 CSP는 cspResourceId 그대로(Azure=ARM ID, GCP=클러스터명, NCP=UUID)
                        String instanceId = TbbResourceIdResolver.k8sInstanceId(
                                cspType, cluster.getCspResourceId(), cluster.getKeyValueList());
                        // AWS인데 Tumblebug 응답에 Arn이 없으면(생성 중이거나 CB-Spider 버전 차이) CUR에서 같은 클러스터의 ARN을 찾는다.
                        // 못 찾으면 클러스터 이름으로 저장되어 CUR과 매칭되지 않으므로 경고를 남긴다.
                        if("AWS".equals(cspType) && !TbbResourceIdResolver.isEksArn(instanceId)){
                            String region = cluster.getConnectionConfig().getRegionZoneInfo() != null ?
                                    cluster.getConnectionConfig().getRegionZoneInfo().getAssignedRegion() : null;
                            String arnFromCur = findEksArnInCur(region, cluster.getCspResourceId());
                            if(arnFromCur != null){
                                log.info("K8s cluster {} (AWS): Arn missing in Tumblebug response, recovered from CUR -> {}", cluster.getId(), arnFromCur);
                                instanceId = arnFromCur;
                            }else{
                                log.warn("K8s cluster {} (AWS): Arn missing in Tumblebug response and not found in CUR (region={}). "
                                        + "Stored as '{}', which will not match CUR until the ARN is available", cluster.getId(), region, instanceId);
                            }
                        }
                        // 계정: AWS=ARN의 account, AZURE=subscription, GCP=SelfLink의 project, 그 외=기본값(mcmpcostopti)
                        String cspAccount = TbbResourceIdResolver.k8sAccount(
                                cspType, instanceId, cluster.getKeyValueList());

                        if(!instanceId.equals(cluster.getCspResourceId())) {
                            log.info("K8s cluster {} ({}): csp_instanceid normalized {} -> {}",
                                    cluster.getId(), cspType, cluster.getCspResourceId(), instanceId);
                        }

                        ResourcegroupMetaModel k8sInfo = ResourcegroupMetaModel.builder()
                                .cspType(cspType)
                                .cspAccount(cspAccount)
                                .cspInstanceid(instanceId)
                                .serviceCd(ns.getId())
                                .serviceNm(ns.getName())
                                .serviceType("K8S")
                                .workspaceCd("ws1")  // TODO: 추후 동적으로 변경 필요
                                // 서버 DB의 servicegroup_meta 는 vm_id·mci_id 가 NOT NULL(기본값 'undefined')이라
                                // NULL 을 넣으면 INSERT 가 거절된다. K8s 행은 MCI 가 없으므로 기본값과 같은 'undefined' 로 채운다.
                                .vmId(cluster.getId() != null ? cluster.getId() : META_UNDEFINED)
                                .vmUid(cluster.getUid())
                                .vmNm(cluster.getName() != null ? cluster.getName() : cluster.getId())
                                .mciId(META_UNDEFINED)
                                .mciUid(null)
                                .mciNm(null)
                                .instanceRunningStatus(k8sStatus)
                                .build();

                        k8sMetaList.add(k8sInfo);
                        k8sMetaList.addAll(buildK8sNodeMeta(ns, cluster, cspType, cspAccount));
                    } catch (Exception e){
                        log.error("Failed to build K8s meta for cluster: {} in namespace: {}, skipped", cluster.getId(), ns.getId(), e);
                    }
                }

                insertK8sMeta(ns, k8sMetaList);
            }

            // Object Storage(버킷) 처리 — service_type = 'OBJECT_STORAGE', csp_instanceid = CSP측 버킷명
            Thread.sleep(2000);
            List<ObjectStorageItemModel> osList = getTBBObjectStorages(ns);

            if(osList != null && !osList.isEmpty()){
                List<ResourcegroupMetaModel> osMetaList = new ArrayList<>();

                for(ObjectStorageItemModel os : osList){
                    if(os == null) continue;

                    // CSP측 버킷명 = Tumblebug uid (cspResourceId는 생성 완료 후 채워지므로 uid로 폴백)
                    String instanceId = TbbResourceIdResolver.objectStorageInstanceId(
                            os.getCspResourceId(), os.getUid(), os.getCspResourceName());
                    if(instanceId == null){
                        log.warn("Object storage {} in ns {} has no usable identifier, skipped", os.getId(), ns.getId());
                        continue;
                    }

                    String cspType = os.getConnectionConfig() != null && os.getConnectionConfig().getProviderName() != null
                            && !os.getConnectionConfig().getProviderName().isBlank()
                        ? os.getConnectionConfig().getProviderName().toUpperCase()
                        : TbbResourceIdResolver.cspTypeFromConnectionName(os.getConnectionName());
                    if(cspType == null){
                        // csp_type 은 PK 의 일부이자 모든 집계의 분기 키 — 알 수 없는 값(UNKNOWN)으로 적재하면 어느 CSP 에도 안 잡히는 죽은 행이 된다
                        log.warn("Object storage {} in ns {} has no resolvable CSP (connection {}), skipped", os.getId(), ns.getId(), os.getConnectionName());
                        continue;
                    }
                    // Tumblebug 버킷 응답에는 CSP 계정 ID 가 없다 → VM 추출 실패 시와 같은 기본값. (connectionName 은 계정 ID 가 아니라 넣지 않음)
                    String cspAccount = TbbResourceIdResolver.DEFAULT_CSP_ACCOUNT;

                    String osStatus = switch (os.getStatus() == null ? "" : os.getStatus()) {
                        case "Deleting", "Failed", "Error", "Inactive", "Deactive" -> "N";
                        default -> "Y";
                    };

                    osMetaList.add(ResourcegroupMetaModel.builder()
                            .cspType(cspType)
                            .cspAccount(cspAccount)
                            .cspInstanceid(instanceId)
                            .serviceCd(ns.getId())
                            .serviceNm(ns.getName())
                            .serviceType("OBJECT_STORAGE")
                            .workspaceCd("ws1")  // TODO: 추후 동적으로 변경 필요
                            // 서버 DB 의 vm_id·mci_id 는 NOT NULL(기본값 'undefined') — K8s 행과 같은 규약(META_UNDEFINED)
                            .vmId(os.getId() != null ? os.getId() : META_UNDEFINED)
                            .vmUid(os.getUid())
                            .vmNm(os.getName() != null ? os.getName() : os.getId())
                            .mciId(META_UNDEFINED)
                            .mciUid(null)
                            .mciNm(null)
                            .instanceRunningStatus(osStatus)
                            .build());
                }

                insertObjectStorageMeta(ns, osMetaList);
            }
        }
    }

    /**
     * 워커 노드 메타 행. 노드 비용을 K8s 비용에 포함하기 위해 노드의 CSP 자원 ID를 service_type = 'K8S' 로 적재한다.
     * vm_id 에는 소속 클러스터 id를 넣어 같은 클러스터의 행끼리 묶는다(클러스터 행과 같은 값).
     * 매칭 기준이 확인된 CSP(지금은 AWS: EC2 인스턴스 ID)만 적재하고, 그 외 CSP는 건너뛴다.
     */
    private List<ResourcegroupMetaModel> buildK8sNodeMeta(TBBNSItemModel ns, K8sClusterItemModel cluster,
                                                          String cspType, String cspAccount){
        List<ResourcegroupMetaModel> rows = new ArrayList<>();
        if(cluster.getK8sNodeGroupList() == null) return rows;

        for(K8sNodeGroupModel nodeGroup : cluster.getK8sNodeGroupList()){
            if(nodeGroup == null || nodeGroup.getK8sNodes() == null) continue;
            String nodeStatus = switch (nodeGroup.getStatus() == null ? "" : nodeGroup.getStatus()) {
                case "Failed", "Deleting", "Inactive", "Deactive", "Error" -> "N";
                default -> "Y";
            };
            for(K8sNodeModel node : nodeGroup.getK8sNodes()){
                if(node == null) continue;
                String nodeInstanceId = TbbResourceIdResolver.k8sNodeInstanceId(cspType, node.getCspResourceId());
                if(nodeInstanceId == null){
                    log.debug("K8s node {} of cluster {} ({}) skipped: node cost matching not supported for this CSP/ID",
                            node.getCspResourceId(), cluster.getId(), cspType);
                    continue;
                }
                rows.add(ResourcegroupMetaModel.builder()
                        .cspType(cspType)
                        .cspAccount(cspAccount)
                        .cspInstanceid(nodeInstanceId)
                        .serviceCd(ns.getId())
                        .serviceNm(ns.getName())
                        .serviceType("K8S")
                        .workspaceCd("ws1")  // TODO: 추후 동적으로 변경 필요
                        .vmId(cluster.getId() != null ? cluster.getId() : META_UNDEFINED)
                        .vmUid(nodeGroup.getId())
                        .vmNm(node.getCspResourceName() != null ? node.getCspResourceName() : nodeInstanceId)
                        .mciId(META_UNDEFINED)
                        .mciUid(null)
                        .mciNm(null)
                        .instanceRunningStatus(nodeStatus)
                        .build());
            }
        }
        return rows;
    }

    /**
     * ns 단위 K8s 메타 적재. 일괄 insert가 실패하면(예: 한 행이 컬럼 길이 초과) 행 단위로 다시 넣어
     * 문제가 된 클러스터만 건너뛴다. 어떤 경우에도 예외를 던지지 않아 이후 Object Storage·다음 ns 처리를 막지 않는다.
     */
    /**
     * Object Storage 메타 적재. insertK8sMeta 와 같은 방식: 배치 INSERT 가 실패하면 행 단위로 재시도해
     * 한 행(예: 너무 긴 csp_instanceid) 때문에 나머지 버킷이 버려지지 않게 한다. 실패는 VM/K8s 결과를 되돌리지 않는다.
     */
    private void insertObjectStorageMeta(TBBNSItemModel ns, List<ResourcegroupMetaModel> osMetaList){
        if(osMetaList.isEmpty()) return;

        try{
            tbbDao.insertTBBServicegroupMeta(osMetaList);
            log.info("Inserted {} object storages for namespace: {}", osMetaList.size(), ns.getId());
            return;
        } catch (Exception e){
            log.error("Batch insert of object storage meta failed for namespace: {}, retrying row by row. error: {}", ns.getId(), e.getMessage());
        }

        int inserted = 0;
        for(ResourcegroupMetaModel row : osMetaList){
            try{
                tbbDao.insertTBBServicegroupMeta(List.of(row));
                inserted++;
            } catch (Exception e){
                log.error("Failed to insert object storage meta: bucket={}, csp_instanceid={}, namespace={}, error: {}",
                        row.getVmId(), row.getCspInstanceid(), ns.getId(), e.getMessage());
            }
        }
        log.info("Inserted {}/{} object storages for namespace: {} (row by row)", inserted, osMetaList.size(), ns.getId());
    }

    private void insertK8sMeta(TBBNSItemModel ns, List<ResourcegroupMetaModel> k8sMetaList){
        if(k8sMetaList.isEmpty()) return;

        try{
            tbbDao.insertTBBServicegroupMeta(k8sMetaList);
            log.info("Inserted {} K8s meta rows (clusters + nodes) for namespace: {}", k8sMetaList.size(), ns.getId());
            k8sMetaList.forEach(this::cleanupStaleAwsK8sMeta);
            return;
        } catch (Exception e){
            log.error("Batch insert of K8s meta failed for namespace: {}, retrying row by row. error: {}", ns.getId(), e.getMessage());
        }

        int inserted = 0;
        for(ResourcegroupMetaModel row : k8sMetaList){
            try{
                tbbDao.insertTBBServicegroupMeta(List.of(row));
                inserted++;
                cleanupStaleAwsK8sMeta(row);
            } catch (Exception e){
                log.error("Failed to insert K8s meta: cluster={}, csp_instanceid={}, namespace={}, error: {}",
                        row.getVmId(), row.getCspInstanceid(), ns.getId(), e.getMessage());
            }
        }
        log.info("Inserted {}/{} K8s meta rows (clusters + nodes) for namespace: {} (row by row)", inserted, k8sMetaList.size(), ns.getId());
    }

    /**
     * Tumblebug 응답에 Arn이 없는 AWS EKS 클러스터의 ARN을 CUR(이번 달·지난달 tbl_table_billing_detail_YYYYMM)에서 찾는다.
     * 후보가 정확히 1개일 때만 반환하고, 없거나 여러 개(같은 이름의 클러스터가 다른 계정/리전에 있음)면 null.
     * 조회 실패는 메타 동기화를 막지 않도록 null로 처리한다.
     */
    private String findEksArnInCur(String region, String clusterName){
        String pattern = TbbResourceIdResolver.eksArnLikePattern(region, clusterName);
        if(pattern == null) return null;

        Set<String> candidates = new LinkedHashSet<>();
        YearMonth now = YearMonth.now();
        DateTimeFormatter yyyyMM = DateTimeFormatter.ofPattern("yyyyMM");
        for(YearMonth ym : List.of(now, now.minusMonths(1))){
            String yearMonth = ym.format(yyyyMM);
            try{
                if(!tbbDao.existsTable("tbl_table_billing_detail_" + yearMonth)) continue;
                candidates.addAll(tbbDao.selectAwsEksArnsFromCur(yearMonth, pattern));
            } catch (Exception e){
                log.warn("Failed to look up EKS ARN in CUR {} for cluster {}: {}", yearMonth, clusterName, e.getMessage());
            }
        }

        if(candidates.size() == 1) return candidates.iterator().next();
        if(candidates.size() > 1){
            log.warn("Multiple EKS ARNs in CUR match cluster {} (region={}): {}. Not guessing", clusterName, region, candidates);
        }
        return null;
    }

    /** AWS 클러스터가 ARN으로 저장된 뒤, 같은 클러스터의 예전 행(클러스터 이름으로 저장된 K8S 행)을 지운다. 실패해도 무시. */
    private void cleanupStaleAwsK8sMeta(ResourcegroupMetaModel row){
        if(!"AWS".equals(row.getCspType()) || !TbbResourceIdResolver.isEksArn(row.getCspInstanceid())) return;
        try{
            int deleted = tbbDao.deleteStaleAwsK8sMeta(row);
            if(deleted > 0){
                log.info("Removed {} stale AWS K8s meta row(s) for cluster {} in namespace {} (replaced by {})",
                        deleted, row.getVmId(), row.getServiceCd(), row.getCspInstanceid());
            }
        } catch (Exception e){
            log.warn("Failed to remove stale AWS K8s meta for cluster {}: {}", row.getVmId(), e.getMessage());
        }
    }

}
