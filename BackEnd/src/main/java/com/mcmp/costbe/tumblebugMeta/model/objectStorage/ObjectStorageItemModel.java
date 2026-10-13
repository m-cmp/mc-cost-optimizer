package com.mcmp.costbe.tumblebugMeta.model.objectStorage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mcmp.costbe.tumblebugMeta.model.k8s.K8sConnectionConfigModel;
import lombok.Data;

/** Tumblebug model.ObjectStorageInfo 중 메타 적재에 필요한 필드만. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectStorageItemModel {
    private String id;
    private String uid;
    private String name;
    private String resourceType;      // "ObjectStorage"
    private String cspResourceId;     // CSP측 버킷명 (cb-spider S3Manager: SystemId = bucketName)
    private String cspResourceName;
    private String connectionName;
    private K8sConnectionConfigModel connectionConfig;
    private String status;
    private String creationDate;
}
