package com.mcmp.costselector.unused.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UnusedSelectReqModel {
    private LocalDateTime create_dt;
    private String resource_id;
}
