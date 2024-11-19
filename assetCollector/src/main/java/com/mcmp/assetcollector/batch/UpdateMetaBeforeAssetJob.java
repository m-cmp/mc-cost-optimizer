package com.mcmp.assetcollector.batch;

import com.mcmp.assetcollector.service.MetaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateMetaBeforeAssetJob implements JobExecutionListener {

    @Autowired
    private MetaService metaService;

    @Override
    public void beforeJob(JobExecution jobExecution){
        try{
            metaService.updateSvcGrpMeta();
        } catch (Exception e){
            log.error("[AssetCollect] Update ServiceGroup Meta Error : {}", e.getMessage());
            e.printStackTrace();
        }

    }
}
