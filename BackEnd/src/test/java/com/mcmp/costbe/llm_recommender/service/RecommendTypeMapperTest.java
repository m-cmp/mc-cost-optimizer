package com.mcmp.costbe.llm_recommender.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RecommendTypeMapperTest {

    private final RecommendTypeMapper mapper = new RecommendTypeMapper();

    @Test
    void mapsEnglishPlans() {
        assertThat(mapper.toEnum("Up")).isEqualTo("upsize");
        assertThat(mapper.toEnum("Down")).isEqualTo("downsize");
        assertThat(mapper.toEnum("Modernize")).isEqualTo("migrate");
        assertThat(mapper.toEnum("Unused")).isEqualTo("terminate");
    }

    @Test
    void mapsKoreanPlans() {
        assertThat(mapper.toEnum("상향")).isEqualTo("upsize");
        assertThat(mapper.toEnum("하향")).isEqualTo("downsize");
        assertThat(mapper.toEnum("최신화")).isEqualTo("migrate");
        assertThat(mapper.toEnum("미사용")).isEqualTo("terminate");
    }

    @Test
    void isCaseInsensitiveAndTrims() {
        assertThat(mapper.toEnum("  up  ")).isEqualTo("upsize");
        assertThat(mapper.toEnum("DOWN")).isEqualTo("downsize");
    }

    @Test
    void unknownPassesThrough_nullBecomesDash() {
        assertThat(mapper.toEnum("weird")).isEqualTo("weird");
        assertThat(mapper.toEnum(null)).isEqualTo("-");
    }
}
