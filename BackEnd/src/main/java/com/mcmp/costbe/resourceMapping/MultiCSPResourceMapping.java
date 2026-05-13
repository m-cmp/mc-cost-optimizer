package com.mcmp.costbe.resourceMapping;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MultiCSPResourceMapping {

    /**
     * 새로운 카테고리 구조: VM, K8S, Others
     * service_type 기반으로 구분하므로 더 이상 서비스명 매핑 불필요
     */
    public static List<String> getAllCategories() {
        return List.of("VM", "K8S", "Others");
    }

    /**
     * @deprecated 더 이상 사용하지 않음. service_type으로 구분.
     */
    @Deprecated
    public static List<String> getServicesByCategory(String csp, String category) {
        return List.of();
    }

    /**
     * @deprecated 더 이상 사용하지 않음. service_type으로 구분.
     */
    @Deprecated
    public static String getCategoryByService(String csp, String serviceName) {
        return "Others";
    }
}