package com.mcmp.cost.ncp.collector.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncExecutorService {

    private final BatchExecutorService batchExecutorService;

    @Async
    public void asyncExecuteBatch(NcpBatchType ncpBatchType) {
        batchExecutorService.executeBatch(ncpBatchType);
    }
}
