package com.mcmp.costbe.llm_recommender.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcmp.costbe.llm_recommender.dao.UnifiedHistoryDao;
import com.mcmp.costbe.llm_recommender.model.UnifiedHistoryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Merges alarm_history (ML) and recommendation_history (LLM) into one history grid
 * at read time. ML plan -> 5-enum, LLM response_json -> detail, sorted date DESC.
 */
@Service
public class UnifiedHistoryService {

    private static final int HISTORY_MAX = 100;

    @Autowired private UnifiedHistoryDao dao;
    @Autowired private RecommendTypeMapper recommendTypeMapper;

    private final ObjectMapper om = new ObjectMapper();

    public List<UnifiedHistoryRow> getUnifiedHistory(String nsId) {
        Map<String, Object> params = Map.of("nsId", nsId, "limit", HISTORY_MAX);

        List<UnifiedHistoryRow> ml = dao.selectAlarmRecommendations(params);
        for (UnifiedHistoryRow r : ml) {
            r.setRecommendType(recommendTypeMapper.toEnum(r.getRecommendType()));
        }

        List<UnifiedHistoryRow> llm = dao.selectLlmRecommendations(params);
        for (UnifiedHistoryRow r : llm) {
            r.setAlarmMessage(extractDetail(r.getResponseJson()));
            r.setResponseJson(null); // drop raw payload after use
        }

        List<UnifiedHistoryRow> all = new ArrayList<>(ml.size() + llm.size());
        all.addAll(ml);
        all.addAll(llm);
        all.sort(Comparator.comparing(UnifiedHistoryRow::getDate,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        return all.size() > HISTORY_MAX ? new ArrayList<>(all.subList(0, HISTORY_MAX)) : all;
    }

    private String extractDetail(String json) {
        if (json == null || json.isBlank()) return "";
        try {
            JsonNode detail = om.readTree(json).get("detail");
            return (detail == null || detail.isNull()) ? "" : detail.asText();
        } catch (Exception e) {
            return "";
        }
    }
}
