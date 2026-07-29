package com.mcmp.costbe.archive.s3;

import com.mcmp.costbe.archive.credential.OpenBaoClient;
import com.mcmp.costbe.cur.exception.CurCredentialNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.util.Map;

/**
 * 아카이브 S3 쓰기용 자격증명 제공자.
 * cost/aws (OpenBao) 에서 mcmp-costopti 전용 키를 읽어 직접 사용. AssumeRole 없음.
 * CUR Setup 미실행 시 CurCredentialNotFoundException 발생.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ArchiveCredentialProvider {

    private final OpenBaoClient openBaoClient;

    public synchronized StaticCredentialsProvider getCredentials() {
        Map<String, String> creds = openBaoClient.readPath("cost/aws");
        String accessKey = creds.get("AWS_ACCESS_KEY_ID");
        String secretKey = creds.get("AWS_SECRET_ACCESS_KEY");

        if (accessKey != null && !accessKey.isBlank()
                && secretKey != null && !secretKey.isBlank()) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey));
        }

        log.error("[archive] cost/aws 자격증명 없음 — CUR Setup 먼저 실행 필요");
        throw new CurCredentialNotFoundException(
                "AWS CUR 자격증명이 없습니다. CUR Setup을 먼저 실행해주세요.");
    }
}
