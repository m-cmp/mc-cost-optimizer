package com.mcmp.collector.batch;

import com.mcmp.collector.dao.AwsDao;
import com.mcmp.collector.model.cur.CurProcessModel;
import com.mcmp.collector.service.Cur;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CurCollect {

    @Autowired
    private Cur cur;

    @Autowired
    private AwsDao awsDao;

    @Autowired
    private BudgetCheckTasklet budgetCheckTasklet;

    @Bean
    public Job curCollectJob(JobRepository jobRepository, Step curCollectStep, Step budgetCheckStep) {
        return new JobBuilder("curCollectJob", jobRepository)
                .start(curCollectStep)
                .next(budgetCheckStep)
                .build();
    }

    @Bean
    @JobScope
    public Step curCollectStep(JobRepository jobRepository, Tasklet curTasklet, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("curCollectStep", jobRepository)
                .tasklet(curTasklet, platformTransactionManager).build();
    }

    @Bean
    @StepScope
    public Tasklet curTasklet(@Value("#{jobParameters['seq']}") String seq){
        return ((contribution, chunkContext) -> {
            log.info(">>>START CUR BATCH JOB");

            LocalDateTime collectDt = LocalDateTime.now();

            List<String> payers = awsDao.getPayerID();
            int run_idx = 0;
            for(String payer : payers){

                if(collectDt.getDayOfMonth() == 1 && seq.equals("1")){
                    CurProcessModel initCURProcess = CurProcessModel.builder()
                            .csp("AWS")
                            .payer_account(payer)
                            .collect_date(String.format("%04d%02d", collectDt.getYear(), collectDt.getMonthValue()))
                            .certifed_fixed_yn("N")
                            .build();

                    awsDao.insertCURProcess(initCURProcess);
                }

                List<CurProcessModel> todos = awsDao.getTodoCURCollectMonth(payer);

                for(CurProcessModel todo : todos){
                    cur.batchInsertCURData(payer, collectDt, seq, todo.getCollect_date(), todo.getObject_key(), run_idx);
                    run_idx += run_idx;
                }

            }

           return RepeatStatus.FINISHED;
        });
    }

    @Bean
    @JobScope
    public Step budgetCheckStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("budgetCheckStep", jobRepository)
                .tasklet(budgetCheckTasklet, platformTransactionManager)
                .build();
    }
}
