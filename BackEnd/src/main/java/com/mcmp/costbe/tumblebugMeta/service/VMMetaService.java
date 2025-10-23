package com.mcmp.costbe.tumblebugMeta.service;

import com.mcmp.costbe.tumblebugMeta.dao.TBBDao;
import com.mcmp.costbe.tumblebugMeta.model.ResourcegroupMetaModel;
import com.mcmp.costbe.tumblebugMeta.model.mci.TBBMCIItemModel;
import com.mcmp.costbe.tumblebugMeta.model.mci.TBBMCIModel;
import com.mcmp.costbe.tumblebugMeta.model.mci.TbVmInfoModel;
import com.mcmp.costbe.tumblebugMeta.model.ns.TBBNSItemModel;
import com.mcmp.costbe.tumblebugMeta.model.ns.TBBNSModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
            String apiUrl = String.format("%s/ns/%s/mci", tumblebugUrl, item.getId());
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

    public TbVmInfoModel getTBBVM(TBBNSItemModel item, TBBMCIItemModel mci, TbVmInfoModel vm){

        if(vm != null){
            String apiUrl = String.format("%s/ns/%s/mci/%s/vm/%s", tumblebugUrl, item.getId(), mci.getId(), vm.getId());
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

    public void getTBBResourceMetaInfo() throws InterruptedException {
        List<TBBNSItemModel> nsList = getTbbNS();

        for(TBBNSItemModel ns : nsList){

            Thread.sleep(2000);
            List<TBBMCIItemModel> mciList = getTBBMCI(ns);

            for(TBBMCIItemModel mci : mciList){
                if("mci".equals(mci.getResourceType())){
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
        }
    }

}
