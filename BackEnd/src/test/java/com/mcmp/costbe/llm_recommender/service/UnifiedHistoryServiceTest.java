package com.mcmp.costbe.llm_recommender.service;

import com.mcmp.costbe.llm_recommender.dao.UnifiedHistoryDao;
import com.mcmp.costbe.llm_recommender.model.UnifiedHistoryRow;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UnifiedHistoryServiceTest {

    private UnifiedHistoryService service(List<UnifiedHistoryRow> ml, List<UnifiedHistoryRow> llm) {
        UnifiedHistoryService s = new UnifiedHistoryService();
        UnifiedHistoryDao dao = new UnifiedHistoryDao() {
            @Override public List<UnifiedHistoryRow> selectAlarmRecommendations(Map<String, Object> p) { return ml; }
            @Override public List<UnifiedHistoryRow> selectLlmRecommendations(Map<String, Object> p) { return llm; }
        };
        ReflectionTestUtils.setField(s, "dao", dao);
        ReflectionTestUtils.setField(s, "recommendTypeMapper", new RecommendTypeMapper());
        return s;
    }

    private UnifiedHistoryRow ml(String date, String plan) {
        UnifiedHistoryRow r = new UnifiedHistoryRow();
        r.setDate(date); r.setAlarmType("ML"); r.setRecommendType(plan); r.setAlarmMessage("ml-note");
        return r;
    }

    private UnifiedHistoryRow llm(String date, String recommendation, String responseJson) {
        UnifiedHistoryRow r = new UnifiedHistoryRow();
        r.setDate(date); r.setAlarmType("LLM"); r.setRecommendType(recommendation); r.setResponseJson(responseJson);
        return r;
    }

    @Test
    void mapsMlRecommendTypeToEnum() {
        UnifiedHistoryService s = service(List.of(ml("2026-06-10 10:00:00", "Up")), List.of());
        List<UnifiedHistoryRow> out = s.getUnifiedHistory("ns-A");
        assertThat(out).hasSize(1);
        assertThat(out.get(0).getRecommendType()).isEqualTo("upsize");
    }

    @Test
    void extractsLlmDetailIntoAlarmMessage() {
        String json = "{\"detail\":\"Move to a smaller type.\",\"recommendation\":\"downsize\"}";
        UnifiedHistoryService s = service(List.of(), List.of(llm("2026-06-10 09:00:00", "downsize", json)));
        List<UnifiedHistoryRow> out = s.getUnifiedHistory("ns-A");
        assertThat(out).hasSize(1);
        assertThat(out.get(0).getAlarmMessage()).isEqualTo("Move to a smaller type.");
        assertThat(out.get(0).getRecommendType()).isEqualTo("downsize");
    }

    @Test
    void mergesAndSortsByDateDesc() {
        UnifiedHistoryService s = service(
                List.of(ml("2026-06-10 08:00:00", "Down")),
                List.of(llm("2026-06-10 12:00:00", "keep", "{\"detail\":\"d\"}")));
        List<UnifiedHistoryRow> out = s.getUnifiedHistory("ns-A");
        assertThat(out).hasSize(2);
        assertThat(out.get(0).getDate()).isEqualTo("2026-06-10 12:00:00"); // newest first
        assertThat(out.get(0).getAlarmType()).isEqualTo("LLM");
        assertThat(out.get(1).getAlarmType()).isEqualTo("ML");
    }

    @Test
    void badJson_yieldsEmptyMessage_doesNotThrow() {
        UnifiedHistoryService s = service(List.of(), List.of(llm("2026-06-10 09:00:00", "keep", "not-json")));
        List<UnifiedHistoryRow> out = s.getUnifiedHistory("ns-A");
        assertThat(out.get(0).getAlarmMessage()).isEqualTo("");
    }

    @Test
    void emptyBothSources_returnsEmpty() {
        UnifiedHistoryService s = service(List.of(), List.of());
        assertThat(s.getUnifiedHistory("ns-A")).isEmpty();
    }
}
