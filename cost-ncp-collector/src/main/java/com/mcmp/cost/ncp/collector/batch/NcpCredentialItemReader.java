package com.mcmp.cost.ncp.collector.batch;

import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.properties.NcpCredentialProperties;
import com.mcmp.cost.ncp.collector.credential.CredentialResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class NcpCredentialItemReader implements ItemReader<NcpApiCredentialDto> {

    private final NcpCredentialProperties ncpCredentialProperties;
    private final CredentialResolver credentialResolver;
    private Iterator<NcpApiCredentialDto> credentialIterator;

    @Override
    public NcpApiCredentialDto read() {
        if (credentialIterator == null) {
            List<NcpApiCredentialDto> credentials = getCredentials();
            credentialIterator = credentials.iterator();
            log.info("Azure credentials loaded: {} items", credentials.size());
        }

        if (credentialIterator.hasNext()) {
            NcpApiCredentialDto azureApiCredentialDto = credentialIterator.next();
            log.debug("Reading credential for tenant.");
            return azureApiCredentialDto;
        }

        return null;
    }

    private List<NcpApiCredentialDto> getCredentials() {
        List<NcpApiCredentialDto> credentials = new ArrayList<>();
        NcpApiCredentialDto ncpApiCredentialDto = new NcpApiCredentialDto();

        // openbao.enabled 정책에 따라 env / OpenBao 에서 결정
        // (NCP OpenBao 키명은 mc-terrarium/terraform-provider-ncloud 관례인 NCLOUD_* 기준 — 실제 적재 키 확인 필요)
        ncpApiCredentialDto.setIamAccessKey(credentialResolver.resolve("ncp", "NCLOUD_ACCESS_KEY", ncpCredentialProperties.getIamAccessKey()));
        ncpApiCredentialDto.setIamSecretKey(credentialResolver.resolve("ncp", "NCLOUD_SECRET_KEY", ncpCredentialProperties.getIamSecretKey()));

        credentials.add(ncpApiCredentialDto);
        return credentials;
    }
}
