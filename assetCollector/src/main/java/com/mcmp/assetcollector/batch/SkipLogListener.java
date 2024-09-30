package com.mcmp.assetcollector.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;

@Slf4j
public class SkipLogListener implements SkipListener<Object, Object> {
    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("Skipped during read due to: {}", t.getMessage(), t);
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        log.warn("Skipped during write for item {} due to: {}", item, t.getMessage(), t);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        log.warn("Skipped during process for item {} due to: {}", item, t.getMessage(), t);
    }
}
