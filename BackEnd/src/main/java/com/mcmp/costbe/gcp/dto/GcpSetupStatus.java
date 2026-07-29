package com.mcmp.costbe.gcp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GcpSetupStatus {
    private final boolean configured;
}
