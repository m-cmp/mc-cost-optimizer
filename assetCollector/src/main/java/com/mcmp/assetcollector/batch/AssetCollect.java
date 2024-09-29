package com.mcmp.assetcollector.batch;


import com.mcmp.assetcollector.model.batch.RSRCAssetComputeMetricModel;
import com.mcmp.assetcollector.model.batch.RunningInstanceModel;
import com.mcmp.assetcollector.model.service.RSRCAssetUsageItemModel;
import com.mcmp.assetcollector.model.service.RSRCAssetUsageModel;
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
import java.util.ArrayList;
import java.util.List;
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

    @Bean
    public SkipLogListener skipLogListener(){
        return new SkipLogListener();
    }

    @Bean
    public Job assetCollectJob(JobRepository jobRepository, Step assetCollectStep) {
        return new JobBuilder("assetCollectJob", jobRepository)
                .start(assetCollectStep)
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

            Thread.sleep(5000);
            List<RSRCAssetComputeMetricModel> data = new ArrayList<>();
            try{
                List<RSRCAssetUsageItemModel> tempData = assetCollectService.getRSRCCpuUsageHistory(item.getNsID(), item.getMciID(), item.getVmID());

                if(!tempData.isEmpty()){
                    for(RSRCAssetUsageItemModel tempDataItem : tempData){
                        try {
                            Double amount = Double.parseDouble(tempDataItem.getValue());

                            RSRCAssetComputeMetricModel dataItem = RSRCAssetComputeMetricModel.builder()
                                    .cspType(item.getCspType())
                                    .cspAccount(item.getCspAccount())
                                    .cspInstanceid(item.getInstanceID())
                                    .collectDt(Timestamp.from(tempDataItem.getTimestamp()))
                                    .metricType("cpu")
                                    .metricAmount(amount)
                                    .resourceStatus("running")
                                    .resourceSpotYn("N")
                                    .build();

                            data.add(dataItem);
                        } catch (NumberFormatException e){
                            log.warn("Invalid number format for NS ID: {}, MIC ID: {}, VM ID: {}, item: {}", item.getNsID(), item.getMciID(), item.getVmID(), item);
                        }
                    }
                } else {
                    return null;
                }

            }catch (Exception ex){
                ex.printStackTrace();
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
}
