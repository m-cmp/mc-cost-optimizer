package com.processor.costprocessor.model.unused;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DailyAssetAmountParamModel {
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private String resourceId;
}
