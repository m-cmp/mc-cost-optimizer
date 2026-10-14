package com.mcmp.collector.batch;

import com.mcmp.collector.service.BudgetCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetCheckTasklet implements Tasklet {

    private final BudgetCheckService budgetCheckService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        budgetCheckService.check();
        return RepeatStatus.FINISHED;
    }
}
