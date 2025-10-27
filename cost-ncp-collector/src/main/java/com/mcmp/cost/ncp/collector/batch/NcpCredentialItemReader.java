package com.mcmp.cost.ncp.collector.batch;

import com.mcmp.cost.ncp.collector.dto.NcpApiCredentialDto;
import com.mcmp.cost.ncp.collector.properties.NcpCredentialProperties;
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

        ncpApiCredentialDto.setIamAccessKey(ncpCredentialProperties.getIamAccessKey());
        ncpApiCredentialDto.setIamSecretKey(ncpCredentialProperties.getIamSecretKey());

        credentials.add(ncpApiCredentialDto);
        return credentials;
    }
}
