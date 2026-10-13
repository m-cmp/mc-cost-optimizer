package com.mcmp.costbe.resourceMapping;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MultiCSPResourceMapping {

    /**
     * 카테고리 구조: VM, K8S, Object Storage, Others (servicegroup_meta.service_type 기준: VM / K8S / OBJECT_STORAGE / 그 외)
     * 순서가 홈·Billing Report 카드의 표시 순서이며, 코드 문자열이 그대로 화면 라벨로 쓰인다.
     * service_type 기반으로 구분하므로 더 이상 서비스명 매핑 불필요
     */
    public static List<String> getAllCategories() {
        return List.of("VM", "K8S", "Object Storage", "Others");
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