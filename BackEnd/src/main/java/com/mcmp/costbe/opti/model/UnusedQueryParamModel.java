package com.mcmp.costbe.opti.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UnusedQueryParamModel extends  UnusedReqModel {
    private LocalDate curDate;
}
