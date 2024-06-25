package com.mcmp.costbe.invoice.model;

import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SummaryReqModel {
    private String today;
    private List<String> selectedProjects;
    private List<String> selectedCsps;
    private String selectedWorkspace;

    private String selectedPeriod;

    private List<LocalDateTime> summaryPeriod;
    private LocalDateTime summaryPeriodDate;
}
