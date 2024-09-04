package com.mcmp.collector.model.aws;

import lombok.Data;

@Data
public class DataExportBucketModel extends UserArnModel{
    private String butcket_name;
}
