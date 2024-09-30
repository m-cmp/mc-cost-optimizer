package com.mcmp.collector.model.unused;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ResourceSetModel {
    private String instanceid;
    private Timestamp createDt;
    private String resourceType;
    private String instanceType;
    private String regionId;
    private String regionNM;
    private String operation;
    private String instanceFamily;
    private String instanceFamilyType;
    private String productSKU;
}
