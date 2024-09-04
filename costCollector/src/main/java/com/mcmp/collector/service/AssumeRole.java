package com.mcmp.collector.service;

import com.mcmp.collector.dao.AwsDao;
import com.mcmp.collector.model.aws.DataExportBucketModel;
import com.mcmp.collector.model.aws.UserArnModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.StsException;

import java.time.Instant;

@Service
@Slf4j
public class AssumeRole {

    @Autowired
    private AwsDao awsDao;


    private final String roleSessionName = "s3-assume-role-" + Instant.now().toEpochMilli();
    private final Region region = Region.AP_NORTHEAST_2;

    public StaticCredentialsProvider assumeRole(String cmpUserId){
        try{
            UserArnModel userModel = awsDao.getUserArn(cmpUserId);
            if(userModel == null || userModel.getRole_arn() == null){
                log.error("No user found with cmpUserId: {}", cmpUserId);
                return null;
            }

            Credentials tempRoleCredentials;
            try( StsClient stsClient = StsClient.builder()
                    .region(region).build()){
                AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                        .roleArn(userModel.getRole_arn())
                        .roleSessionName(roleSessionName)
                        .build();
                AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
                tempRoleCredentials = roleResponse.credentials();
            }

            String key = tempRoleCredentials.accessKeyId();
            String secKey = tempRoleCredentials.secretAccessKey();
            String secToken = tempRoleCredentials.sessionToken();

            StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(key, secKey, secToken)
            );

            return staticCredentialsProvider;

        } catch (StsException | S3Exception e){
            log.error(e.getMessage());
            throw new RuntimeException();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
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
