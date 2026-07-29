package com.mcmp.costbe.cur.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurSetupStatus {
    private final boolean costCredsStored;
    private final boolean dbRegistered;
    private final String mailReceiver;
}
