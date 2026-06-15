package com.mcmp.azure.vm.rightsizer.credential;

import com.mcmp.azure.vm.rightsizer.properties.AzureCredentialProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Azure 크레덴셜 빈을 CredentialResolver(openbao.enabled 정책)로 생성한다.
 * 모든 소비처(writer, AzureUtils 등)는 이 빈을 주입받으므로 자동으로 정책이 반영된다.
 * (AzureCredentialProperties 는 @ConfigurationProperties 자동바인딩을 끄고 여기서 직접 구성)
 */
@Configuration
public class AzureCredentialConfig {

    @Bean
    public AzureCredentialProperties azureCredentialProperties(
            CredentialResolver resolver,
            @Value("${azure.credential.tenant-id:}") String tenantId,
            @Value("${azure.credential.client-id:}") String clientId,
            @Value("${azure.credential.client-secret:}") String clientSecret,
            @Value("${azure.credential.subscription-id:}") String subscriptionId) {

        return new AzureCredentialProperties(
                resolver.resolve("azure", "ARM_TENANT_ID", tenantId),
                resolver.resolve("azure", "ARM_CLIENT_ID", clientId),
                resolver.resolve("azure", "ARM_CLIENT_SECRET", clientSecret),
                resolver.resolve("azure", "ARM_SUBSCRIPTION_ID", subscriptionId));
    }
}
