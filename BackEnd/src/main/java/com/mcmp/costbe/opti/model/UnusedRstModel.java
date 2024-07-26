package com.mcmp.costbe.opti.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UnusedRstModel extends UnusedReqModel {

    private List<UnusedQueryRstModel> unusedRec;
    private LocalDate curDate;
}
