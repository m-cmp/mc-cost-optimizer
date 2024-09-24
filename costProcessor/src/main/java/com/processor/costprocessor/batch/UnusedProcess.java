package com.processor.costprocessor.batch;

import com.processor.costprocessor.dao.UnusedDao;
import com.processor.costprocessor.model.unused.DailyAssetAmountModel;
import com.processor.costprocessor.model.unused.DailyAssetAmountParamModel;
import com.processor.costprocessor.model.unused.MetricAvgModel;
import com.processor.costprocessor.model.unused.ResourceSetModel;
import com.processor.costprocessor.model.util.CurrentDateModel;
import com.processor.costprocessor.util.CalDatetime;
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
import org.springframework.http.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UnusedProcess {
    @Autowired
    @Qualifier("sqlSessionBatch")
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private CalDatetime calDatetime;

    @Autowired
    private UnusedDao unusedDao;

    @Value("${cost.selector.url}")
    private String selectURL;

    @Bean
    public Job unusedProcessJob(JobRepository jobRepository, Step processStep) {
        return new JobBuilder("unusedProcessJob", jobRepository)
                .start(processStep)
                .build();
    }

    @Bean
    public CustomerItemListener customerItemListener(){
        return new CustomerItemListener();
    }

    @Bean
    @JobScope
    public Step processStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("processStep", jobRepository)
                .<ResourceSetModel, List<DailyAssetAmountModel>>chunk(1, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .startLimit(2)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(2)
                .skip(Exception.class)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .listener(customerItemListener())
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<ResourceSetModel> reader() {
        CurrentDateModel curDt = calDatetime.currentDatetime();

        Map<String, Object> params = new HashMap<>();
        params.put("startDT", curDt.getCurStartDatetime());
        params.put("endDT", curDt.getCurEndDatetime());

        return new MyBatisPagingItemReaderBuilder<ResourceSetModel>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("aws.getVerifyResources")
                .parameterValues(params)
                .pageSize(1)
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<ResourceSetModel, List<DailyAssetAmountModel>> processor() {
        CurrentDateModel dateRange = calDatetime.monthDateRange();

        return item -> {
            DailyAssetAmountParamModel param = new DailyAssetAmountParamModel();
            param.setResourceId(item.getInstanceid());
            param.setStartDt(dateRange.getCurStartDatetime());
            param.setEndDt(dateRange.getCurEndDatetime());

            List<DailyAssetAmountModel> dailyAssetAmounts = unusedDao.getDailyAssetAmount(param);

            return dailyAssetAmounts;
        };
    }

    @Bean
    @StepScope
    public CompositeItemWriter<List<DailyAssetAmountModel>> writer() {
        List<ItemWriter<? super List<DailyAssetAmountModel>>> writers = Stream.of(
                writerListItems(writerItems()),
                writerTask2()
        ).collect(Collectors.toList());

        return new CompositeItemWriterBuilder<List<DailyAssetAmountModel>>()
                .delegates(writers)
                .build();
    }

    @Bean
    public MyBatisBatchItemWriter<DailyAssetAmountModel> writerItems() {
        return new MyBatisBatchItemWriterBuilder<DailyAssetAmountModel>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("aws.insertDailyAssetAmount")
                .build();
    }

    @Bean
    public ItemWriter<List<DailyAssetAmountModel>> writerListItems(MyBatisBatchItemWriter<DailyAssetAmountModel> writerItems){
        return chunks -> {
            for(List<DailyAssetAmountModel> chunk : chunks){
                writerItems.write(new Chunk<>(chunk));
            }
        };
    }


    @Bean
    public ItemWriter<List<DailyAssetAmountModel>> writerTask2() {
        return itemLists -> {
            for (List<DailyAssetAmountModel> itemList : itemLists) {
                try{

                    String apiUrl = String.format("%s/batunused/selectRsp", selectURL);
                    RestTemplate restTemplate = new RestTemplate();

                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                    Map<String, Object> body = new HashMap<>();
                    body.put("resource_id", itemList.get(0).getResource_id());
                    body.put("create_dt", ZonedDateTime.now().toLocalDateTime());
                    HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);

                    restTemplate.exchange(apiUrl, HttpMethod.POST, httpEntity, String.class);
                } catch (HttpClientErrorException | HttpServerErrorException clientError) {
                    String cleintErrorMsg = clientError.getMessage();
                    log.error("FAIL TO CALL COST SELECTOR - UNUSED API : " + cleintErrorMsg);
                }

//                for(DailyAssetAmountModel item : itemList){
//                    log.info("Writer: " + item);
//                }
            }
        };
    }

}
