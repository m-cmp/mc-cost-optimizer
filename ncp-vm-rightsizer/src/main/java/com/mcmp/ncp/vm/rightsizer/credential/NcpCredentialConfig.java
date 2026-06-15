package com.mcmp.ncp.vm.rightsizer.credential;

import com.mcmp.ncp.vm.rightsizer.properties.NcpCredentialProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * NCP 크레덴셜 빈을 CredentialResolver(openbao.enabled 정책)로 생성한다.
 * 모든 소비처가 이 빈을 주입받으므로 자동으로 정책이 반영된다.
 *
 * NOTE: NCP OpenBao 키명(NCLOUD_ACCESS_KEY/NCLOUD_SECRET_KEY)은 mc-terrarium/terraform-provider-ncloud
 *       관례 기준 추정값이다. 실제 적재 키 확인 후 필요 시 수정할 것.
 */
@Configuration
public class NcpCredentialConfig {

    @Bean
    public NcpCredentialProperties ncpCredentialProperties(
            CredentialResolver resolver,
            @Value("${ncp.credential.iam-access-key:}") String iamAccessKey,
            @Value("${ncp.credential.iam-secret-key:}") String iamSecretKey) {

        return new NcpCredentialProperties(
                resolver.resolve("ncp", "NCLOUD_ACCESS_KEY", iamAccessKey),
                resolver.resolve("ncp", "NCLOUD_SECRET_KEY", iamSecretKey));
    }
}
