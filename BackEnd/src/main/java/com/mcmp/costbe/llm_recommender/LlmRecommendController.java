package com.mcmp.costbe.llm_recommender;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.llm_recommender.model.RecommendRequest;
import com.mcmp.costbe.llm_recommender.model.Recommendation;
import com.mcmp.costbe.llm_recommender.service.LlmRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/costopti/be/llm_recommender")
@Tag(name = "LLM Recommender", description = "LLM resource resizing recommendation API")
public class LlmRecommendController {

    @Autowired
    private LlmRecommendService llmRecommendService;

    @PostMapping(path = "/recommend")
    @Operation(summary = "LLM 자원 추천", description = "인스턴스 1개의 분석 점수를 LLM에 질의해 리사이징 추천을 반환한다.")
    public ResponseEntity recommend(@RequestBody RecommendRequest req) {
        ResultModel result = new ResultModel();
        try {
            Recommendation data = llmRecommendService.recommend(
                    req.getInstanceId(), req.getModel(), req.getUserQuestion());
            result.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(500, "Failed to get LLM recommendation");
        }
        return ResponseEntity.ok(result);
    }
}
