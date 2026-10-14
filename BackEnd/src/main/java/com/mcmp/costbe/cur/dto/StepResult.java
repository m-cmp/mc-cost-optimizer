package com.mcmp.costbe.cur.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StepResult {
    private final String name;
    private final String status;  // OK | SKIP | FAILED | PENDING
    private final String message;

    public static StepResult ok(String name) {
        return new StepResult(name, "OK", null);
    }

    public static StepResult ok(String name, String message) {
        return new StepResult(name, "OK", message);
    }

    public static StepResult skip(String name, String reason) {
        return new StepResult(name, "SKIP", reason);
    }

    public static StepResult failed(String name, String reason) {
        return new StepResult(name, "FAILED", reason);
    }

    public static StepResult warn(String name, String message) {
        return new StepResult(name, "WARN", message);
    }

    public static StepResult pending(String name) {
        return new StepResult(name, "PENDING", null);
    }
}
