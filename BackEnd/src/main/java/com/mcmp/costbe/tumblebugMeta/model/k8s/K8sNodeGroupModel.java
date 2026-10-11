package com.mcmp.costbe.tumblebugMeta.model.k8s;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Tumblebug K8sNodeGroupInfo 중 메타 적재에 쓰는 필드 (src/core/model/k8s_cluster.go).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class K8sNodeGroupModel {
    private String id;
    private String name;
    private String status;
    private List<K8sNodeModel> k8sNodes;
}
