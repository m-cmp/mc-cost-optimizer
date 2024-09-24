package com.processor.costprocessor.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;


public class DelayItemWriter<T> implements ItemWriter<T> {

    private final long delayInMillis;

    public DelayItemWriter(long delayInMillis) {
        this.delayInMillis = delayInMillis;
    }

    @Override
    public void write(Chunk<? extends T> items) throws Exception {
        Thread.sleep(delayInMillis);
    }
}
