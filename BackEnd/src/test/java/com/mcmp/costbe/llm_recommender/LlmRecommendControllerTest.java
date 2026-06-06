package com.mcmp.costbe.llm_recommender;

import com.mcmp.costbe.llm_recommender.model.Recommendation;
import com.mcmp.costbe.llm_recommender.service.LlmRecommendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LlmRecommendController.class)
class LlmRecommendControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private LlmRecommendService service;

    @Test
    void recommend_returnsResultModelDataWithRecommendation() throws Exception {
        Recommendation r = new Recommendation();
        r.setInstance("i-real");
        r.setRecommendation("downsize");
        r.setConfidence("high");
        r.setStatus("ok");
        when(service.recommend(eq("i-real"), any())).thenReturn(r);

        mvc.perform(post("/api/costopti/be/llm_recommender/recommend")
                .contentType("application/json")
                .content("{\"instanceId\":\"i-real\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("OK"))      // ResultModel transport status
            .andExpect(jsonPath("$.Data.instance").value("i-real"))
            .andExpect(jsonPath("$.Data.recommendation").value("downsize"))
            .andExpect(jsonPath("$.Data.status").value("ok")); // domain status
    }
}
