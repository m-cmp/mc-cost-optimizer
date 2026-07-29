package com.mcmp.costbe.gcp.dto;

import com.mcmp.costbe.cur.dto.StepResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GcpSetupResult {
    private final String projectId;
    private final String dataset;
    private final String table;
    private final List<StepResult> steps;
}
