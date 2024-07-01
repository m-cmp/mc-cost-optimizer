package com.mcmp.costbe.resourceMapping.aws;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AWSResourceMapping {

    private static final Map<String, List<String>> dataMap = new HashMap<>();

    static {
        dataMap.put("Virtual Machine", List.of("AmazonECS", "AmazonEC2"));
        dataMap.put("Storage", List.of("AmazonS3", "AmazonEBS", "AmazonEFS"));
        dataMap.put("Database", List.of("AmazoneDynamoDB", "AmazonRDS"));
        dataMap.put("LB", List.of("AWSELB"));
    }

    public static List<String> getData(String key) {
        return dataMap.getOrDefault(key, List.of());
    }
}
