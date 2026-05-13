package com.mcmp.costbe.tumblebugMeta.model.k8s;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class K8sClusterListModel {
    @JsonProperty("K8sClusterInfo")
    private List<K8sClusterItemModel> clusters;
}
