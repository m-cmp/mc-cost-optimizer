package com.mcmp.collector.model.unused;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class ResourceSetParamModel {
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private String account;
    private String yearMonth;
}
