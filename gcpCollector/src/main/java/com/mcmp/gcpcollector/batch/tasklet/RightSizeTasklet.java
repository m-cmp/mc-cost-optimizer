package com.mcmp.gcpcollector.batch.tasklet;

import com.mcmp.gcpcollector.service.GcpRightSizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
@RequiredArgsConstructor
public class RightSizeTasklet implements Tasklet {

    private final GcpRightSizeService gcpRightSizeService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        gcpRightSizeService.detect();
        return RepeatStatus.FINISHED;
    }
}
