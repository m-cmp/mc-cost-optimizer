package com.mcmp.cost.azure.collector.batch;

import com.mcmp.cost.azure.collector.dto.AzureApiCredentialDto;
import com.mcmp.cost.azure.collector.properties.AzureCredentialProperties;
import com.mcmp.cost.azure.collector.credential.CredentialResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class AzureCredentialItemReader implements ItemReader<AzureApiCredentialDto> {

    private final AzureCredentialProperties azureCredentialProperties;
    private final CredentialResolver credentialResolver;
    private Iterator<AzureApiCredentialDto> credentialIterator;

    @Override
    public AzureApiCredentialDto read() {
        if (credentialIterator == null) {
            List<AzureApiCredentialDto> credentials = getCredentials();
            credentialIterator = credentials.iterator();
            log.info("Azure credentials loaded: {} items", credentials.size());
        }

        if (credentialIterator.hasNext()) {
            AzureApiCredentialDto azureApiCredentialDto = credentialIterator.next();
            log.debug("Reading credential for tenant: {}", azureApiCredentialDto.getTenantId());
            return azureApiCredentialDto;
        }

        return null;
    }

    private List<AzureApiCredentialDto> getCredentials() {
        List<AzureApiCredentialDto> credentials = new ArrayList<>();
        AzureApiCredentialDto azureApiCredentialDto = new AzureApiCredentialDto();

        // openbao.enabled 정책에 따라 env / OpenBao 에서 크레덴셜 결정 (OpenBao 키: ARM_*)
        azureApiCredentialDto.setTenantId(credentialResolver.resolve("azure", "ARM_TENANT_ID", azureCredentialProperties.getTenantId()));
        azureApiCredentialDto.setClientId(credentialResolver.resolve("azure", "ARM_CLIENT_ID", azureCredentialProperties.getClientId()));
        azureApiCredentialDto.setClientSecret(credentialResolver.resolve("azure", "ARM_CLIENT_SECRET", azureCredentialProperties.getClientSecret()));
        azureApiCredentialDto.setSubscriptionId(credentialResolver.resolve("azure", "ARM_SUBSCRIPTION_ID", azureCredentialProperties.getSubscriptionId()));

        credentials.add(azureApiCredentialDto);
        return credentials;
    }
}
