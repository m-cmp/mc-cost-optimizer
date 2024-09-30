package com.mcmp.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.AnomalyDateInterval;
import software.amazon.awssdk.services.costexplorer.model.GetAnomaliesRequest;
import software.amazon.awssdk.services.costexplorer.model.GetAnomaliesResponse;

@Slf4j
@Service
public class Abnormal {

    @Autowired
    private AssumeRole assumeRole;

    public void getCostAnomaly(String payerId){
        StaticCredentialsProvider staticCredentialsProvider = assumeRole.assumeRole(payerId);

        try(CostExplorerClient costExplorerClient = CostExplorerClient.builder()
                .credentialsProvider(staticCredentialsProvider)
                .region(Region.US_EAST_1)  // 현재 여기에서만 cost and billing 확인 가능
                .build()){
            GetAnomaliesRequest anomaliesRequest = GetAnomaliesRequest.builder()
                    .dateInterval(AnomalyDateInterval.builder()
                            .startDate("2024-08-30")
                            .endDate("2024-09-20")
                            .build())
                    .build();

            GetAnomaliesResponse anomaliesResponse = costExplorerClient.getAnomalies(anomaliesRequest);

            System.out.println("이상비용탐지 api 결과 : " + anomaliesResponse);

            anomaliesResponse.anomalies().forEach(anomaly -> {
                System.out.println("Anomaly ID: " + anomaly.anomalyId());
                System.out.println("Anomaly Score: " + anomaly.anomalyScore());
                System.out.println("Impact: " + anomaly.impact());
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
