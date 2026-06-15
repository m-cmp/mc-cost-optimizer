package com.mcmp.assetcollector.batch;


import com.mcmp.assetcollector.model.batch.RSRCAssetComputeMetricModel;
import com.mcmp.assetcollector.model.batch.RunningInstanceModel;
import com.mcmp.assetcollector.model.service.RSRCAssetUsageItemModel;
import com.mcmp.assetcollector.model.service.RSRCAssetUsageModel;
import com.mcmp.assetcollector.model.service.influxMetric.InfluxMetricRstItem;
import com.mcmp.assetcollector.service.AssetCollectService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PrimitiveIterator;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AssetCollect {

    @Autowired
    @Qualifier("sqlSessionBatch")
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private AssetCollectService assetCollectService;

    @Autowired
    private UpdateMetaBeforeAssetJob updateMetaBeforeAssetJob;

    @Bean
    public SkipLogListener skipLogListener(){
        return new SkipLogListener();
    }

    @Bean
    public Job assetCollectJob(JobRepository jobRepository, Step assetCollectStep) {
        return new JobBuilder("assetCollectJob", jobRepository)
                .start(assetCollectStep)
                .listener(updateMetaBeforeAssetJob)
                .build();
    }

    @Bean
    @JobScope
    public Step assetCollectStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("assetCollectStep", jobRepository)
                .<RunningInstanceModel, List<RSRCAssetComputeMetricModel>>chunk(1, platformTransactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writerListItems(writerItems()))
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
    public MyBatisPagingItemReader<RunningInstanceModel> reader() {
        return new MyBatisPagingItemReaderBuilder<RunningInstanceModel>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("asset.getAssetRunningInstance")
                .pageSize(1)
                .build();
    }

    @StepScope
    public ItemProcessor<RunningInstanceModel, List<RSRCAssetComputeMetricModel>> processor() {
        return item -> {

//            Thread.sleep(5000);
            List<RSRCAssetComputeMetricModel> data = new ArrayList<>();
            try{
                List<InfluxMetricRstItem> tempData = assetCollectService.getRSRCCpuUsageHistory(item.getNsID(), item.getMciID(), item.getVmID());

                if(!tempData.isEmpty()){
                    InfluxMetricRstItem totalAssetData = null;

                    for(InfluxMetricRstItem aitem : tempData){
                        if("cpu".equals(aitem.getName())){
                            totalAssetData = aitem;
                            break;
                        }
                    }

                    if(totalAssetData == null){
                        return null;
                    }

                    int timestampIndex = findIndex(totalAssetData.getColumns(), "timestamp");
                    int valueIndex = findIndex(totalAssetData.getColumns(), "usage_idle");

                    if(!totalAssetData.getValues().isEmpty()){
                        for(List<Object> value : totalAssetData.getValues()){
                            try{
                                String timestampString = (String) value.get(timestampIndex);
                                OffsetDateTime offsetDateTime = OffsetDateTime.parse(timestampString, DateTimeFormatter.ISO_DATE_TIME);
                                Timestamp timestamp = Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());

                                if(value.get(valueIndex) == null){
                                    log.warn("item : {}'s value is null when {}", item, timestamp);
                                    continue;
                                }
                                Double asset_idle_Value = (Double) value.get(valueIndex);
                                Double roundedValue = Math.round(asset_idle_Value * 100.0) / 100.0;
                                Double usageValue = 100 - roundedValue;

                                RSRCAssetComputeMetricModel dataItem = RSRCAssetComputeMetricModel.builder()
                                        .cspType(item.getCspType())
                                        .cspAccount(item.getCspAccount())
                                        .cspInstanceid(item.getInstanceID())
                                        .collectDt(timestamp)
                                        .metricType(totalAssetData.getName())  // cpu..
                                        .metricAmount(usageValue)
                                        .resourceStatus("running")
                                        .resourceSpotYn("N")
                                        .build();

                                data.add(dataItem);

                            } catch (NumberFormatException e){
                                log.warn("Invalid number format for NS ID: {}, MIC ID: {}, VM ID: {}, item: {}", item.getNsID(), item.getMciID(), item.getVmID(), item);
                            }
                        }
                    } else {
                        log.warn("Empty Asset Value for Timestamp: {}(UTC), NS ID: {}, MIC ID: {}, VM ID: {}, item: {}", LocalDateTime.now(ZoneId.of("UTC")), item.getNsID(), item.getMciID(), item.getVmID(), item);
                    }

                } else {
                    return null;
                }

            }catch (Exception ex){
                log.warn("Error for Asset Collect NS ID: {}, MIC ID: {}, VM ID: {}, error: {}", item.getNsID(), item.getMciID(), item.getVmID(), ex.getStackTrace());
                return null;
            }
            return data;
        };
    }

    @Bean
    public ItemWriter<List<RSRCAssetComputeMetricModel>> writerListItems(MyBatisBatchItemWriter<RSRCAssetComputeMetricModel> writerItems){
        return chunks -> {
            for(List<RSRCAssetComputeMetricModel> chunk : chunks){
                writerItems.write(new Chunk<>(chunk));
            }
        };
    }

    @Bean
    public MyBatisBatchItemWriter<RSRCAssetComputeMetricModel> writerItems() {
        return new MyBatisBatchItemWriterBuilder<RSRCAssetComputeMetricModel>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("asset.insertRSRCComputeMetric")
                .build();
    }

    public int findIndex(List<String> columns, String target){
        return columns.indexOf(target);
    }
}
