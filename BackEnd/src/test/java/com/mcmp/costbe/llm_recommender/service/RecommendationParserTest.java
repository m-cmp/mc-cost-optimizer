package com.mcmp.costbe.llm_recommender.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecommendationParserTest {

    private final RecommendationParser parser = new RecommendationParser();

    @Test
    void parsesPlainJson() {
        String text = "{\"instance\":\"i-1\",\"recommendation\":\"downsize\","
                + "\"detail\":\"Move to a smaller type\",\"reasoning\":\"P95 and P99 low\","
                + "\"confidence\":\"high\"}";
        var r = parser.parse(text);
        assertThat(r.getRecommendation()).isEqualTo("downsize");
        assertThat(r.getConfidence()).isEqualTo("high");
        assertThat(r.getDetail()).isNotBlank();
        assertThat(r.getReasoning()).isNotBlank();
    }

    @Test
    void parsesFencedJson() {
        String text = "```json\n{\"instance\":\"i-1\",\"recommendation\":\"keep\","
                + "\"detail\":\"d\",\"reasoning\":\"r\",\"confidence\":\"low\"}\n```";
        var r = parser.parse(text);
        assertThat(r.getRecommendation()).isEqualTo("keep");
    }

    @Test
    void rejectsBrokenJson() {
        assertThatThrownBy(() -> parser.parse("not json at all"))
                .isInstanceOf(RecommendationParseException.class);
    }

    @Test
    void rejectsInvalidEnum() {
        String text = "{\"instance\":\"i-1\",\"recommendation\":\"explode\","
                + "\"detail\":\"d\",\"reasoning\":\"r\",\"confidence\":\"high\"}";
        assertThatThrownBy(() -> parser.parse(text))
                .isInstanceOf(RecommendationParseException.class);
    }

    @Test
    void rejectsInvalidConfidence() {
        String text = "{\"instance\":\"i-1\",\"recommendation\":\"keep\","
                + "\"detail\":\"d\",\"reasoning\":\"r\",\"confidence\":\"very-high\"}";
        assertThatThrownBy(() -> parser.parse(text))
                .isInstanceOf(RecommendationParseException.class);
    }

    @Test
    void parsesOptionalAnswerWhenPresent() {
        String text = "{\"instance\":\"i-1\",\"recommendation\":\"keep\",\"detail\":\"d\","
                + "\"reasoning\":\"r\",\"confidence\":\"high\",\"answer\":\"Cost is already efficient.\"}";
        var r = parser.parse(text);
        assertThat(r.getAnswer()).isEqualTo("Cost is already efficient.");
    }

    @Test
    void answerIsNullWhenAbsent() {
        String text = "{\"instance\":\"i-1\",\"recommendation\":\"keep\",\"detail\":\"d\","
                + "\"reasoning\":\"r\",\"confidence\":\"high\"}";
        var r = parser.parse(text);
        assertThat(r.getAnswer()).isNull();
    }
}
