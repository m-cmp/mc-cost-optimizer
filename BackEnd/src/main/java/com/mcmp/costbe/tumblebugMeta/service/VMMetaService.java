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

            if(!response.getNs().isEmpty()){
                return response.getNs();
            }else{
                log.warn("TUMBLEBUG META - NS IS EMPTY");
                return new ArrayList<>();
            }
        } catch (HttpClientErrorException | HttpServerErrorException clientError) {
            HttpStatus statusCode = clientError.getStatusCode();
            log.error("FAIL TO GET TUMBLEBUG META - NS : " + statusCode);
            throw new RuntimeException();
        } catch (Exception e){
            log.error("FAIL TO GET TUMBLEBUG META - NS : " +e.getMessage());
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

                if(!response.getMci().isEmpty()){
                    return response.getMci();
                }else{
                    log.warn("TUMBLEBUG META - MCI => MCI IS EMPTY");
                    return new ArrayList<>();
                }
            } catch (HttpClientErrorException | HttpServerErrorException clientError) {
                HttpStatus statusCode = clientError.getStatusCode();
                log.error("FAIL TO GET TUMBLEBUG META - MCI => NS ID : {}, error code : {}", item.getId(), statusCode);
                throw new RuntimeException();
            } catch (Exception e){
                log.error("FAIL TO GET TUMBLEBUG META - MCI => NS ID : {}", item.getId());
                throw new RuntimeException();
            }

        } else {
            log.error("[ERROR] : GET TUMBLEBUG META - MCI => NS IS EMPTY");
            return new ArrayList<>();
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
                        List<ResourcegroupMetaModel> resourcegroupMetaList = new ArrayList<>();

                        List<TbVmInfoModel> vmList = mci.getVm();
                        for(TbVmInfoModel vm : vmList){
                            String vmStatus;
                            if(!vm.getStatus().isEmpty()){
                                vmStatus = switch (vm.getStatus()){
                                    case "Running" -> "Y";
                                    case "Failed" -> "N";
                                    default -> "Y";
                                };
                            }else {
                                vmStatus = "Y";
                            }

                            if(vm.getCspResourceId() != null){
                                ResourcegroupMetaModel vmInfo = ResourcegroupMetaModel.builder()
                                        .cspType("AWS")
                                        .cspAccount("mcmpcostopti")
                                        .cspInstanceid(vm.getCspResourceId())
                                        .serviceCd(ns.getId())
                                        .serviceNm(ns.getName())
                                        .serviceUid(ns.getUid())
                                        .vmId(vm.getId())
                                        .vmUid(vm.getUid())
                                        .vmNm(vm.getName())
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

                    } catch (Exception e){
                        e.printStackTrace();
                        throw new RuntimeException();
                    }

                }
            }
        }
    }

}
