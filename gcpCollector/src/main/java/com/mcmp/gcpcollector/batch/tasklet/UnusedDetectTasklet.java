package com.mcmp.gcpcollector.batch.tasklet;

import com.mcmp.gcpcollector.service.GcpUnusedDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
@RequiredArgsConstructor
public class UnusedDetectTasklet implements Tasklet {

    private final GcpUnusedDetectionService gcpUnusedDetectionService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        gcpUnusedDetectionService.detect();
        return RepeatStatus.FINISHED;
    }
}
