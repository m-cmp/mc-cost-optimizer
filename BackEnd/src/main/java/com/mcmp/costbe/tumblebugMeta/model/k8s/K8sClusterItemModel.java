package com.mcmp.costbe.tumblebugMeta.model.k8s;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class K8sClusterItemModel {
    private String id;
    private String uid;
    private String name;
    private String description;
    private String resourceType;
    private String cspResourceId;
    private String cspResourceName;
    private String connectionName;
    private K8sConnectionConfigModel connectionConfig;
    private String status;
    private String version;
    private String createdTime;
    /**
     * cb-spider KeyValueList pass-through (Tumblebug k8s_cluster.go: convertSpiderKeyValueListToTbKeyValueList).
     * AWS EKS: {key:"Arn", value:"arn:aws:eks:..."} 등. 과금 데이터 매칭 키 정규화에 사용.
     */
    private List<Map<String, String>> keyValueList;
    /** 노드 그룹과 워커 노드 목록. 노드 비용을 K8s 비용에 포함할 때 노드 ID(cspResourceId)를 메타에 적재한다. */
    private List<K8sNodeGroupModel> k8sNodeGroupList;
}
