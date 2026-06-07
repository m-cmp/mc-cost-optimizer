package com.mcmp.costbe.llm_recommender.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendRateLimiterTest {

    @Test
    void allowsUpToLimit_thenBlocks() {
        RecommendRateLimiter rl = new RecommendRateLimiter();
        ReflectionTestUtils.setField(rl, "limitPerMin", 3);

        assertThat(rl.tryAcquire()).isTrue();   // 1
        assertThat(rl.tryAcquire()).isTrue();   // 2
        assertThat(rl.tryAcquire()).isTrue();   // 3
        assertThat(rl.tryAcquire()).isFalse();  // 4 -> over budget
    }

    @Test
    void windowReset_allowsAgain() {
        RecommendRateLimiter rl = new RecommendRateLimiter();
        ReflectionTestUtils.setField(rl, "limitPerMin", 1);

        assertThat(rl.tryAcquire()).isTrue();
        assertThat(rl.tryAcquire()).isFalse();

        // simulate the current window having started over a minute ago
        ReflectionTestUtils.setField(rl, "windowStartMs", System.currentTimeMillis() - 61_000L);

        assertThat(rl.tryAcquire()).isTrue(); // fresh window -> allowed again
    }
}
