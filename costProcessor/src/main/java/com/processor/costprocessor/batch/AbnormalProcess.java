package com.processor.costprocessor.batch;

import com.processor.costprocessor.model.abnormal.AbnormalItemModel;
import com.processor.costprocessor.model.unused.DailyAssetAmountModel;
import com.processor.costprocessor.model.util.AlarmReqModel;
import com.processor.costprocessor.util.AlarmService;
import com.processor.costprocessor.util.CalDatetime;
import com.processor.costprocessor.util.NumericCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AbnormalProcess {

    @Autowired
    @Qualifier("sqlSessionBatch")
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private CalDatetime calDatetime;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private NumericCalculator numericCalculator;

    @Bean
    public CustomerItemListener customerListener(){
        return new CustomerItemListener();
    }


    @Bean
    public Job abnormalProcessJob(JobRepository jobRepository, Step abnormalProcessStep) {
        return new JobBuilder("abnormalProcessJob", jobRepository)
                .start(abnormalProcessStep)
                .build();
    }

    @Bean
    @JobScope
    public Step abnormalProcessStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("abnormalProcessStep", jobRepository)
                .<AbnormalItemModel, AbnormalItemModel>chunk(50, platformTransactionManager)
                .reader(abnormalReader())
                .processor(abnormalProcessor())
                .writer(abnormalWriterItems())
                .startLimit(2)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(2)
                .skip(Exception.class)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .listener(customerListener())
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<AbnormalItemModel> abnormalReader(){
        Map<String, Object> param = new HashMap<>();
        param.put("standardDT", calDatetime.getYesterDate().toString());
        param.put("subjectDTs", calDatetime.getLastMonthWeeklyDates().stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList()));

        return new MyBatisPagingItemReaderBuilder<AbnormalItemModel>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("abnoraml.getAbnormalSubjectsCost")
                .parameterValues(param)
                .pageSize(50)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<AbnormalItemModel, AbnormalItemModel> abnormalProcessor(){
        LocalDate collect_dt = LocalDate.now();

        return item -> {
            double percent;
            if(item.getSubject_cost() != 0){
                percent = ((item.getStandard_cost() - item.getSubject_cost()) / item.getSubject_cost()) * 100;
            } else {
                percent = 0;
            }
            item.setPercentage_point(percent);

            if(percent >= 30){
                item.setAbnormal_rating("Critical");
            } else if(percent >= 20){
                item.setAbnormal_rating("Caution");
            } else if(percent >= 10){
                item.setAbnormal_rating("Warning");
            } else {
                return null;
            }

            item.setCollect_dt(collect_dt);

            AlarmReqModel alarmReqModel = AlarmReqModel.builder()
                    .event_type("Abnormal")
                    .resource_id(item.getProduct_cd())
                    .resource_type(item.getProduct_cd())
                    .csp_type(item.getCsp_type())
                    .urgency("Warning")
                    .plan(item.getAbnormal_rating())
                    .note("지난달 비용(" + numericCalculator.parseExponentialFormat(item.getSubject_cost()) + " USD) 대비 이번달 비용(" + numericCalculator.parseExponentialFormat(item.getStandard_cost()) + " USD)이 " + item.getPercentage_point() + " % 발생했습니다.")
                    .project_cd(item.getProject_cd())
                    .build();

            alarmService.sendAlarm(alarmReqModel);

            return item;
        };
    }

    @Bean
    public MyBatisBatchItemWriter<AbnormalItemModel> abnormalWriterItems() {
        return new MyBatisBatchItemWriterBuilder<AbnormalItemModel>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("abnoraml.insertDailyAbnoramlCost")
                .build();
    }


}
