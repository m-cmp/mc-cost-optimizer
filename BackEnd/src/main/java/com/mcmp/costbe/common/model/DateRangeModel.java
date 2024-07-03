package com.mcmp.costbe.common.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DateRangeModel {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
