package com.mcmp.collector.batch;

import com.mcmp.collector.dao.UnusedDao;
import com.mcmp.collector.model.unused.AccountModel;
import com.mcmp.collector.model.unused.ResourceSetModel;
import com.mcmp.collector.model.unused.ResourceSetParamModel;
import jdk.jfr.Name;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UnusedCollect {

    @Autowired
    @Qualifier("sqlSessionBatch")
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private UnusedDao unusedDao;

    @Autowired
    private final UnusedJobParameter unusedJobParameter;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("unused-collect-task-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Job unusedCollectJob(JobRepository jobRepository, Step collectStep) {
        return new JobBuilder("unusedCollectJob", jobRepository)
                .start(collectStep)
                .build();
    }

    @Bean("JobParameter")
    @JobScope
    public UnusedJobParameter jobParameter(@Value("#{jobParameters[startDt]}") String startDatetime, @Value("#{jobParameters[endDt]}") String endDatetime){
        return new UnusedJobParameter(startDatetime, endDatetime);
    }

    @Bean
    public SkipLogListener skipLogListener(){
        return new SkipLogListener();
    }

    @Bean
    @JobScope
    public Step collectStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("collectStep", jobRepository)
                .<AccountModel, Future<List<ResourceSetModel>>>chunk(5, platformTransactionManager)
                .reader(reader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .startLimit(2)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(2)
                .skip(Exception.class)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .listener(skipLogListener())
                .build();
    }


    @Bean
    @StepScope
    public MyBatisPagingItemReader<AccountModel> reader() {
        return new MyBatisPagingItemReaderBuilder<AccountModel>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("batch.getAccounts")
                .pageSize(5)
                .build();
    }
    @StepScope
    public ItemProcessor<AccountModel, List<ResourceSetModel>> processor(LocalDateTime startDatetime, LocalDateTime endDatetime) {
        return item -> {
            ResourceSetParamModel param = new ResourceSetParamModel();
            param.setAccount(item.getAccount());
            param.setStartDt(startDatetime);
            param.setEndDt(endDatetime);

            List<ResourceSetModel> data = unusedDao.getResourceSet(param);

            return data;
        };
    }

    @Bean
    @StepScope
    public AsyncItemProcessor<AccountModel, List<ResourceSetModel>> asyncItemProcessor() {
        AsyncItemProcessor<AccountModel, List<ResourceSetModel>> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(processor(unusedJobParameter.getStartDt(), unusedJobParameter.getEndDt()));
        asyncItemProcessor.setTaskExecutor(threadPoolTaskExecutor());
        return asyncItemProcessor;
    }

    @Bean
    public MyBatisBatchItemWriter<ResourceSetModel> writerItems() {
        return new MyBatisBatchItemWriterBuilder<ResourceSetModel>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("batch.insertRst")
                .build();
    }

    @Bean
    public ItemWriter<List<ResourceSetModel>> writerListItems(MyBatisBatchItemWriter<ResourceSetModel> writerItems){
        return chunks -> {
            for(List<ResourceSetModel> chunk : chunks){
                writerItems.write(new Chunk<>(chunk));
            }
        };
    }


    @Bean
    public ItemWriter<List<ResourceSetModel>> writerTask2() {
        return itemLists -> {
            for (List<ResourceSetModel> itemList : itemLists) {
                for(ResourceSetModel item : itemList){
                    System.out.println("Writer: 쓴다 " + item);
                }
            }
        };
    }

    @Bean
    @StepScope
    public CompositeItemWriter<List<ResourceSetModel>> writer() {
        List<ItemWriter<? super List<ResourceSetModel>>> writers = Stream.of(
                writerListItems(writerItems()),
                writerTask2()
        ).collect(Collectors.toList());

        return new CompositeItemWriterBuilder<List<ResourceSetModel>>()
                .delegates(writers)
                .build();
    }

    @Bean
    @StepScope
    public AsyncItemWriter<List<ResourceSetModel>> asyncItemWriter(){
        AsyncItemWriter<List<ResourceSetModel>> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(writer());

        return asyncItemWriter;
    }
}
