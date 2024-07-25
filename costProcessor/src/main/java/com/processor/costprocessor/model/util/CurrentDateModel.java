package com.processor.costprocessor.model.util;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CurrentDateModel {
    private LocalDateTime curStartDatetime;
    private LocalDateTime curEndDatetime;
}
