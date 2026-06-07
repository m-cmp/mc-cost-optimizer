package com.mcmp.costbe.llm_recommender.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Fixed-window in-memory rate limiter for recommend calls — a cost guard.
 *
 * The /recommend endpoint is per-instance and has no server-side cap otherwise:
 * the 5-instance limit lives only in the frontend and is bypassable by calling
 * the API directly (or by a buggy/looping client). Each call costs LLM tokens,
 * so this bounds how many calls can be triggered per minute and fails closed
 * (rejects without calling the LLM) once the budget is spent.
 */
@Component
public class RecommendRateLimiter {

    @Value("${llm.recommend.rate-limit-per-min:30}")
    private int limitPerMin;

    private int count = 0;
    private long windowStartMs = System.currentTimeMillis();

    /** @return true if a call is allowed; false if this minute's budget is spent. */
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        if (now - windowStartMs >= 60_000L) { // new minute -> reset the window
            windowStartMs = now;
            count = 0;
        }
        if (count >= limitPerMin) {
            return false;
        }
        count++;
        return true;
    }
}
