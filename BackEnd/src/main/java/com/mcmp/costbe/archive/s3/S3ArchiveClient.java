package com.mcmp.costbe.archive.s3;

import com.mcmp.costbe.archive.config.ArchiveS3Properties;
import com.mcmp.costbe.archive.dao.ArchiveDao;
import com.mcmp.costbe.archive.model.HeadResult;
import com.mcmp.costbe.archive.model.PutResult;
import com.mcmp.costbe.archive.model.UserArnModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

/**
 * 아카이브 S3 쓰기/검증 클라이언트.
 *   - 버킷 = temp_cmp_user_role_arn(재사용), 접근 = AssumeRole 세션 크레덴셜
 *   - 키 = {prefix}/{relativeKey}  (CUR 데이터와 prefix 로 분리)
 *   - 업로드는 SSE(AES256)
 *   - 리전 = crossRegionAccessEnabled(true) 로 SDK 가 버킷 실제 리전을 자동 판별+재서명.
 *     배포마다 버킷이 다른 리전에 있어도 설정 불필요. archive.s3.region 은 초기 엔드포인트(base)일 뿐.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3ArchiveClient {

    private final ArchiveDao archiveDao;
    private final ArchiveCredentialProvider credentialProvider;
    private final ArchiveS3Properties props;

    private String fullKey(String relativeKey) {
        return props.getPrefix() + "/" + relativeKey;
    }

    private S3Client build(UserArnModel u) {
        StaticCredentialsProvider creds = credentialProvider.getCredentials();
        if (creds == null) return null;
        // crossRegion: base 리전으로 시작하되 버킷이 다른 리전이면 SDK 가 리다이렉트로 자동 보정.
        return S3Client.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(creds)
                .crossRegionAccessEnabled(true)
                .build();
    }

    /** 오브젝트 업로드. 실패 시 null. */
    public PutResult put(String relativeKey, byte[] body) {
        if (body == null) {
            log.error("[archive] put 본문 null (relativeKey={})", relativeKey);
            return null;
        }
        UserArnModel u = archiveDao.getAwsUserArn();
        if (u == null || u.getBucketNm() == null) {
            log.error("[archive] 버킷 조회 실패 (temp_cmp_user_role_arn)");
            return null;
        }
        String key = fullKey(relativeKey);
        try (S3Client s3 = build(u)) {
            if (s3 == null) return null;
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(u.getBucketNm())
                    .key(key)
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .contentType("application/gzip")
                    .build();
            s3.putObject(req, RequestBody.fromBytes(body));
            log.info("[archive] put s3://{}/{} ({} bytes)", u.getBucketNm(), key, body.length);
            return new PutResult(u.getBucketNm(), key, body.length);
        } catch (Exception e) {
            log.error("[archive] put 실패 s3://{}/{} - {}", u.getBucketNm(), key, e.getMessage());
            return null;
        }
    }

    /** 오브젝트 HEAD 검증. 존재 여부 + byteSize. 오류 시 null. */
    public HeadResult head(String relativeKey) {
        UserArnModel u = archiveDao.getAwsUserArn();
        if (u == null || u.getBucketNm() == null) return null;
        String key = fullKey(relativeKey);
        try (S3Client s3 = build(u)) {
            if (s3 == null) return null;
            HeadObjectResponse resp = s3.headObject(HeadObjectRequest.builder()
                    .bucket(u.getBucketNm()).key(key).build());
            long size = resp.contentLength() != null ? resp.contentLength() : 0L;
            return new HeadResult(u.getBucketNm(), key, size, true);
        } catch (S3Exception e) {
            // 404(없음)는 정상 흐름 — 검증 시 "미존재"로 리턴 (NoSuchKeyException 은 S3Exception 하위)
            if (e.statusCode() == 404) {
                return new HeadResult(u.getBucketNm(), key, 0L, false);
            }
            log.error("[archive] head 실패 s3://{}/{} ({}): {}", u.getBucketNm(), key, e.statusCode(), e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("[archive] head 오류 s3://{}/{} - {}", u.getBucketNm(), key, e.getMessage());
            return null;
        }
    }
}
