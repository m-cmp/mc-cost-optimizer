package com.mcmp.collector.model.unused;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ResourceSetModel {
    private String instanceid;
    private Timestamp createDt;
}
