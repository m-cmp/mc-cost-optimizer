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
            applyResponse(r);
            r.setResponseJson(null); // drop raw payload after use
        }

        List<UnifiedHistoryRow> all = new ArrayList<>(ml.size() + llm.size());
        all.addAll(ml);
        all.addAll(llm);
        all.sort(Comparator.comparing(UnifiedHistoryRow::getDate,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        return all.size() > HISTORY_MAX ? new ArrayList<>(all.subList(0, HISTORY_MAX)) : all;
    }

    /**
     * Fills the message (and, for non-ok rows, the recommendType) from the LLM response_json
     * so failed rows explain themselves instead of showing two blank cells.
     *   ok               -> alarmMessage = detail (recommendType keeps its enum)
     *   error            -> recommendType = "error",        alarmMessage = error reason
     *   insufficient_data -> recommendType = "insufficient", alarmMessage = short note
     */
    private void applyResponse(UnifiedHistoryRow r) {
        String json = r.getResponseJson();
        if (json == null || json.isBlank()) { r.setAlarmMessage(""); return; }
        try {
            JsonNode root = om.readTree(json);
            String detail = text(root, "detail");
            if (!detail.isBlank()) {            // ok row: one-sentence action
                r.setAlarmMessage(detail);
                return;
            }
            String status = text(root, "status");
            if ("error".equals(status)) {       // surface WHY instead of a blank row
                r.setRecommendType("error");
                String err = text(root, "error");
                r.setAlarmMessage(err.isBlank() ? "Recommendation failed." : err);
            } else if ("insufficient_data".equals(status)) {
                r.setRecommendType("insufficient");
                r.setAlarmMessage("Insufficient usage data for a recommendation.");
            } else {
                r.setAlarmMessage("");
            }
        } catch (Exception e) {
            r.setAlarmMessage("");
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? "" : v.asText();
    }
}
