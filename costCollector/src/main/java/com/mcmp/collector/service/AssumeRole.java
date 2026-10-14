package com.mcmp.collector.service;

import com.mcmp.collector.credential.OpenBaoClient;
import com.mcmp.collector.dao.AwsDao;
import com.mcmp.collector.model.aws.DataExportBucketModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.util.Map;

@Service
@Slf4j
public class AssumeRole {

    @Autowired
    private AwsDao awsDao;

    @Autowired
    private OpenBaoClient openBaoClient;

    /**
     * cost/aws (OpenBao) 에서 mcmp-costopti 전용 키를 읽어 직접 반환. AssumeRole 없음.
     * CUR Setup 미실행(cost/aws 없음) 시 RuntimeException 발생 — null 반환하지 않음(NPE 방지).
     */
    public StaticCredentialsProvider assumeRole(String cmpUserId) {
        Map<String, String> costCreds = openBaoClient.readPath("cost/aws");
        String accessKey = costCreds.get("AWS_ACCESS_KEY_ID");
        String secretKey = costCreds.get("AWS_SECRET_ACCESS_KEY");

        if (accessKey != null && !accessKey.isBlank()
                && secretKey != null && !secretKey.isBlank()) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey));
        }

        log.error("cost/aws 자격증명 없음 — CUR Setup 먼저 실행 필요 (cmpUserId={})", cmpUserId);
        throw new RuntimeException("cost/aws 자격증명 없음. CUR Setup을 먼저 실행해주세요.");
    }

    public String getDataExportBucketNM(String cmpUserID){
        DataExportBucketModel bucketModel = awsDao.getExportBucket(cmpUserID);

        if(bucketModel != null){
            return bucketModel.getButcket_name();
        }else {
            log.error("No Bucket Name found - User : " + cmpUserID);
            throw new RuntimeException();
        }
    }

}
