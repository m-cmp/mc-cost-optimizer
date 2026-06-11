package com.mcmp.gcpcollector.batch.writer;

import com.mcmp.gcpcollector.dao.GcpBillingDao;
import com.mcmp.gcpcollector.dto.GcpBillingRawDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class GcpBillingItemWriter implements ItemWriter<GcpBillingRawDto> {

    private final GcpBillingDao gcpBillingDao;

    @Override
    public void write(Chunk<? extends GcpBillingRawDto> chunk) {
        gcpBillingDao.insertBatch(new ArrayList<>(chunk.getItems()));
        log.debug("gcp_billing_raw 적재 - {}건", chunk.getItems().size());
    }
}
