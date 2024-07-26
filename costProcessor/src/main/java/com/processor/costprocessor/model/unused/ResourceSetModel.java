package com.processor.costprocessor.model.unused;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResourceSetModel {
    private String instanceid;
    private LocalDateTime createDt;
}
