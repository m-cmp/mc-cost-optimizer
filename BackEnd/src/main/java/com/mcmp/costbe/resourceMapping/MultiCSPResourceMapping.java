package com.mcmp.costbe.resourceMapping;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MultiCSPResourceMapping {

    private static final Map<String, Map<String, List<String>>> cspResourceMap = new HashMap<>();

    static {
        // AWS 매핑
        Map<String, List<String>> awsMap = new HashMap<>();
        awsMap.put("Virtual Machine", List.of("AmazonECS", "AmazonEC2", "AWSLambda", "AmazonEKS"));
        awsMap.put("Storage", List.of("AmazonS3", "AmazonEBS", "AmazonEFS", "AmazonGlacier"));
        awsMap.put("Database", List.of("AmazoneDynamoDB", "AmazonRDS", "AmazonRedshift", "AmazonElastiCache"));
        awsMap.put("LB", List.of("AWSELB", "AmazonVPC", "AmazonRoute53", "AmazonCloudFront", "AWSDataTransfer"));
        awsMap.put("Others", List.of()); // 동적으로 추가될 예정
        cspResourceMap.put("AWS", awsMap);

        // NCP 매핑
        Map<String, List<String>> ncpMap = new HashMap<>();
        ncpMap.put("Virtual Machine", List.of("Server(VPC)", "Server", "Container Registry", "Kubernetes Service", "Cloud Functions"));
        ncpMap.put("Storage", List.of("Block Storage", "Object Storage", "NAS", "Archive Storage"));
        ncpMap.put("Database", List.of("Cloud DB for MySQL", "Cloud DB for Redis", "Cloud DB for MongoDB", "Elasticsearch Service"));
        ncpMap.put("LB", List.of("Load Balancer (VPC)", "Load Balancer", "Public IP", "Virtual Private Cloud", "Global DNS", "Network - Server&LoadBalancer", "Network - Object Storage", "NAT Gateway"));
        ncpMap.put("Others", List.of("Cloud Log Analytics", "Certificate Manager", "Cloud Outbound Mailer", "Software"));
        cspResourceMap.put("NCP", ncpMap);

        // Azure 매핑
        Map<String, List<String>> azureMap = new HashMap<>();
        azureMap.put("Virtual Machine", List.of("Virtual Machines", "Virtual Machines Licenses", "Container Instances", "Kubernetes Service", "App Service"));
        azureMap.put("Storage", List.of("Storage", "Backup", "Disk Storage", "File Storage", "Blob Storage"));
        azureMap.put("Database", List.of("SQL Database", "Cosmos DB", "MySQL Database", "PostgreSQL Database", "Redis Cache"));
        azureMap.put("LB", List.of("Application Gateway", "Load Balancer", "VPN Gateway", "Virtual Network", "NAT Gateway", "Network Watcher", "Azure DNS", "Bandwidth", "Traffic Manager"));
        azureMap.put("Others", List.of("Azure Monitor", "Event Hubs", "Key Vault", "Service Bus"));
        cspResourceMap.put("AZURE", azureMap);
    }

    public static List<String> getServicesByCategory(String csp, String category) {
        return cspResourceMap.getOrDefault(csp, new HashMap<>()).getOrDefault(category, List.of());
    }

    public static String getCategoryByService(String csp, String serviceName) {
        Map<String, List<String>> cspMap = cspResourceMap.get(csp);
        if (cspMap == null) return "Others";

        for (Map.Entry<String, List<String>> entry : cspMap.entrySet()) {
            if (entry.getValue().contains(serviceName)) {
                return entry.getKey();
            }
        }

        // 패턴 기반 분류
        String lowerServiceName = serviceName.toLowerCase();
        if (lowerServiceName.contains("vm") || lowerServiceName.contains("server") ||
            lowerServiceName.contains("compute") || lowerServiceName.contains("container")) {
            return "Virtual Machine";
        } else if (lowerServiceName.contains("storage") || lowerServiceName.contains("disk") ||
                   lowerServiceName.contains("blob") || lowerServiceName.contains("nas")) {
            return "Storage";
        } else if (lowerServiceName.contains("database") || lowerServiceName.contains("db") ||
                   lowerServiceName.contains("sql") || lowerServiceName.contains("redis")) {
            return "Database";
        } else if (lowerServiceName.contains("load") || lowerServiceName.contains("network") ||
                   lowerServiceName.contains("vpc") || lowerServiceName.contains("gateway") ||
                   lowerServiceName.contains("dns") || lowerServiceName.contains("bandwidth")) {
            return "LB";
        }

        return "Others";
    }

    public static List<String> getAllCategories() {
        return List.of("Virtual Machine", "Storage", "Database", "LB", "Others");
    }
}