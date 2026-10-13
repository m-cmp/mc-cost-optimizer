package com.mcmp.costbe.tumblebugMeta.model.k8s;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class K8sClusterListModel {
    @JsonProperty("K8sClusterInfo")
    private List<K8sClusterItemModel> clusters;
}
