package com.mcmp.costbe.tumblebugMeta.model.objectStorage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/** GET /ns/{nsId}/resources/objectStorage 응답 (model.ObjectStorageListResponse). */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectStorageListModel {
    @JsonProperty("objectStorage")
    private List<ObjectStorageItemModel> objectStorages;
}
