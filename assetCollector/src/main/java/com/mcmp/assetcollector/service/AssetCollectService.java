package com.mcmp.assetcollector.service;

import com.mcmp.assetcollector.model.service.RSRCAssetUsageDataModel;
import com.mcmp.assetcollector.model.service.RSRCAssetUsageItemModel;
import com.mcmp.assetcollector.model.service.RSRCAssetUsageModel;
import com.mcmp.assetcollector.model.service.influxMetric.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class AssetCollectService {

    @Value("${asset.collect.url}")
    private String assetCollectUrl;

//    public List<RSRCAssetUsageItemModel> getRSRCCpuUsageHistory(String nsID, String mciID, String vmID){
//        String apiUrl = String.format("%s/api/o11y/monitoring/%s/%s/target/%s/csp/cpu_usage", assetCollectUrl, nsID, mciID, vmID);
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders httpHeaders = new HttpHeaders();
//        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
//
//        try{
//            ResponseEntity<RSRCAssetUsageModel> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, RSRCAssetUsageModel.class);
//            RSRCAssetUsageModel response = responseEntity.getBody();
//            RSRCAssetUsageDataModel data = response.getData();
//
//            if(data == null || data.getTimestampValues() == null || data.getTimestampValues().isEmpty()){
//                log.warn("RSRC Asset Usage - {} => NS ID : {}, MIC ID : {}, VM ID : {} => response : {}", (data == null ? "DATA IS EMPTY" : "TimestampValues IS EMPTY"),  nsID, mciID, vmID, response);
//                return new ArrayList<>();
//            }
//            return data.getTimestampValues();
//
//        } catch (HttpClientErrorException | HttpServerErrorException clientError) {
//            HttpStatusCode statusCode = clientError.getStatusCode();
//            log.error("FAIL TO GET RSRC Asset Usage - NS ID : {}, MIC ID : {}, VM ID : {} => {}", nsID, mciID, vmID, statusCode);
//            return new ArrayList<>();
//        } catch (Exception e){
//            log.error("FAIL TO GET RSRC Asset Usage - NS ID : {}, MIC ID : {}, VM ID : {}", nsID, mciID, vmID);
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//
//    }

    public List<InfluxMetricRstItem> getRSRCCpuUsageHistory(String nsID, String mciID, String vmID){
        String apiUrl = String.format("%s/api/o11y/monitoring/influxdb/metric/%s/%s/%s", assetCollectUrl, nsID, mciID, vmID);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        InfluxMetricReqModel body = new InfluxMetricReqModel().defaultValue();

        InfluxMetricField mtr_mean = InfluxMetricField.builder().function("mean").field("usage_idle").build();

        InfluxMetricCondition cdt_cpu = InfluxMetricCondition.builder().key("cpu").value("cpu-total").build();

        body.setFields(Arrays.asList(mtr_mean));
        body.setConditions(Arrays.asList(cdt_cpu));

        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

        try{
            ResponseEntity<InfluxMetricRst> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, InfluxMetricRst.class);
            InfluxMetricRst response = responseEntity.getBody();
            List<InfluxMetricRstItem> data = response.getData();

            if(data == null || data.isEmpty()){
                log.warn("RSRC Asset Usage - {} => NS ID : {}, MIC ID : {}, VM ID : {} => response : {}", (data == null ? "DATA IS EMPTY" : "TimestampValues IS EMPTY"),  nsID, mciID, vmID, response);
                return new ArrayList<>();
            }
            return data;

        } catch (HttpClientErrorException | HttpServerErrorException clientError) {
            HttpStatusCode statusCode = clientError.getStatusCode();
            log.error("FAIL TO GET RSRC Asset Usage - NS ID : {}, MIC ID : {}, VM ID : {} => {}", nsID, mciID, vmID, statusCode);
            return new ArrayList<>();
        } catch (Exception e){
            log.error("FAIL TO GET RSRC Asset Usage - NS ID : {}, MIC ID : {}, VM ID : {}", nsID, mciID, vmID);
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
}
