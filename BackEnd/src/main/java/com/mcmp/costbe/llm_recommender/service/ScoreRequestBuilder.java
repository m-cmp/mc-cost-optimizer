package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.model.score.ScoreRequest;
import com.mcmp.costbe.llm_recommender.model.score.ScoreSample;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds POST /score request bodies (LLM_DEV_SPEC.html section 4). The real
 * resource list only gives us instance_id; interval_seconds and CPU/MEM samples
 * aren't collected yet, so they're filled with a fixed mock usage profile until
 * that data source exists.
 */
@Component
public class ScoreRequestBuilder {

    private static final int INTERVAL_SECONDS = 3600;

    private static final double[] CPU_PROFILE = {
            32.1, 30.5, 29.8, 31.2, 33.4, 38.7, 45.2, 52.6,
            58.3, 61.5, 63.2, 64.8, 65.1, 64.3, 62.7, 60.9,
            58.4, 54.2, 49.6, 44.3, 40.1, 37.2, 35.0, 33.3
    };

    private static final double[] MEM_PROFILE = {
            48.0, 47.5, 47.2, 47.8, 48.5, 50.1, 53.4, 56.8,
            59.2, 60.7, 61.5, 62.0, 62.3, 61.9, 61.0, 59.8,
            58.1, 55.9, 53.2, 51.0, 49.5, 48.8, 48.3, 48.1
    };

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ScoreRequest build(String instanceId) {
        ScoreRequest req = new ScoreRequest();
        req.setInstanceId(instanceId);
        req.setIntervalSeconds(INTERVAL_SECONDS);
        req.setSamples(buildSamples());
        return req;
    }

    private List<ScoreSample> buildSamples() {
        LocalDateTime start = LocalDateTime.now()
                .withMinute(0).withSecond(0).withNano(0)
                .minusHours(CPU_PROFILE.length - 1);

        List<ScoreSample> samples = new ArrayList<>(CPU_PROFILE.length);
        for (int i = 0; i < CPU_PROFILE.length; i++) {
            ScoreSample sample = new ScoreSample();
            sample.setTimestamp(start.plusHours(i).format(TIMESTAMP_FORMAT));
            sample.setCpuUsage(CPU_PROFILE[i]);
            sample.setMemUsage(MEM_PROFILE[i]);
            samples.add(sample);
        }
        return samples;
    }
}
