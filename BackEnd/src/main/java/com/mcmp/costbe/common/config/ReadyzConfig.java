package com.mcmp.costbe.common.config;

import com.mcmp.costbe.common.model.ReadyzModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class ReadyzConfig {

    @Resource(name="sqlSessionTemplateBill")
    private SqlSessionTemplate sqltemplate;

    @Value("${costopti.alarmservice.url}")
    private String alarmServiceUrl;

    @Value("${costopti.assetcollector.url}")
    private String assetCollectorUrl;

    @Value("${costopti.costcollector.url}")
    private String costCollectorUrl;

    @Value("${costopti.costprocessor.url}")
    private String costProcessorUrl;

    @Value("${costopti.costselector.url}")
    private String costSelectorUrl;


    private String readyzStatus;


    @PostConstruct
    public void init(){
        this.readyzStatus = (isDBConnection()) ? "OK" : "BE DB CONNECTION FAIL";
    }

    public String getState() {
        String healthStatus;

        Map<String, Object> alarmStatus = checkExternalServiceHealth(alarmServiceUrl + "/api/costopti/alert/healthcheck");
        Map<String, Object> assetCLTStatus = checkExternalServiceHealth(assetCollectorUrl + "/api/costopti/assetclt/healthcheck");
        Map<String, Object> costCLTStatus = checkExternalServiceHealth(costCollectorUrl + "/api/costopti/costctl/healthcheck");
        Map<String, Object> costPRSStatus = checkExternalServiceHealth(costProcessorUrl + "/api/costopti/costprs/healthcheck");
        Map<String, Object> costSLTStatus = checkExternalServiceHealth(costSelectorUrl + "/api/costopti/costslt/healthcheck");

        boolean isAlarmHealthy = "UP".equals(alarmStatus.get("status"));
        boolean isAssetCLTHealthy = "UP".equals(assetCLTStatus.get("status"));
        boolean isCostCLTHealthy = "UP".equals(costCLTStatus.get("status"));
        boolean isCostPRSHealthy = "UP".equals(costPRSStatus.get("status"));
        boolean isCostSLTHealthy = "UP".equals(costSLTStatus.get("status"));

        if(isAlarmHealthy && isAssetCLTHealthy && isCostCLTHealthy && isCostPRSHealthy && isCostSLTHealthy){
            healthStatus = "OK";
        } else {
            StringBuilder message = new StringBuilder("One or more systems are down: ");
            if (!isAlarmHealthy) {
                message.append("Alarm Service is down. ");
            }
            if (!isAssetCLTHealthy) {
                message.append("Asset Collector is down. ");
            }
            if (!isCostCLTHealthy) {
                message.append("Cost Collector is down. ");
            }
            if (!isCostPRSHealthy) {
                message.append("Cost Processor is down. ");
            }
            if (!isCostSLTHealthy) {
                message.append("Cost Selector is down. ");
            }
            healthStatus = message.toString().trim();
        }

        return healthStatus;
    }

    public synchronized void setState(ReadyzModel newState) {
        this.readyzStatus = newState.getStatus();
    }

    public boolean isDBConnection(){
        try(SqlSession session = sqltemplate.getSqlSessionFactory().openSession()){
            session.getConnection().isValid(3);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Map<String, Object> checkExternalServiceHealth(String externalServiceUrl) {
        Map<String, Object> externalServiceStatus = new HashMap<>();
        boolean isHealthy;

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(externalServiceUrl, HttpMethod.GET, entity, String.class);

            isHealthy = (response.getStatusCodeValue() == 200);
        } catch (Exception e) {
            isHealthy = false;
            log.error("Error checking health for external service: " + externalServiceUrl, e);
        }

        externalServiceStatus.put("status", isHealthy ? "UP" : "DOWN");

        return externalServiceStatus;
    }
}
