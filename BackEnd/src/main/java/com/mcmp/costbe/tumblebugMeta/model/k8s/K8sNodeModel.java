package com.mcmp.costbe.tumblebugMeta.model.k8s;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Tumblebug K8sNodeInfo. 워커 노드의 CSP 자원 ID (AWS EKS = EC2 인스턴스 ID i-…).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class K8sNodeModel {
    private String cspResourceId;
    private String cspResourceName;
}
