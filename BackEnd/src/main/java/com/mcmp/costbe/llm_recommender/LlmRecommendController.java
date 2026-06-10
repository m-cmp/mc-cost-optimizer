package com.mcmp.costbe.llm_recommender;

import com.mcmp.costbe.common.model.ResultModel;
import com.mcmp.costbe.llm_recommender.config.LlmModelProperties;
import com.mcmp.costbe.llm_recommender.model.RecommendRequest;
import com.mcmp.costbe.llm_recommender.model.Recommendation;
import com.mcmp.costbe.llm_recommender.service.LlmRecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/costopti/be/llm_recommender")
@Tag(name = "LLM Recommender", description = "LLM resource resizing recommendation API")
public class LlmRecommendController {

    @Autowired
    private LlmRecommendService llmRecommendService;

    @Autowired
    private LlmModelProperties modelProperties;

    @Autowired
    private com.mcmp.costbe.llm_recommender.service.UnifiedHistoryService unifiedHistoryService;

    @PostMapping(path = "/recommend")
    @Operation(summary = "LLM 자원 추천", description = "인스턴스 1개의 분석 점수를 LLM에 질의해 리사이징 추천을 반환한다.")
    public ResponseEntity recommend(@RequestBody RecommendRequest req) {
        ResultModel result = new ResultModel();
        try {
            Recommendation data = llmRecommendService.recommend(
                    req.getInstanceId(), req.getProvider(), req.getModel(), req.getUserQuestion(), req.getNsId());
            result.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(500, "Failed to get LLM recommendation");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/models")
    @Operation(summary = "선택 가능 모델 목록", description = "프로바이더별 선택 가능한 모델 목록을 설정(llm.models.*) 기반으로 반환한다.")
    public ResponseEntity models() {
        ResultModel result = new ResultModel();
        result.setData(modelProperties.getModels());
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/history")
    @Operation(summary = "추천 이력 조회", description = "네임스페이스(nsId)별 LLM 추천 이력을 최신순(최대 100건)으로 반환한다.")
    public ResponseEntity history(@RequestParam String nsId) {
        ResultModel result = new ResultModel();
        result.setData(llmRecommendService.getHistory(nsId));
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/history/unified")
    @Operation(summary = "통합 추천 이력 조회",
            description = "alarm_history(ML)와 recommendation_history(LLM)를 합쳐 nsId별로 최신순 반환한다.")
    public ResponseEntity unifiedHistory(@RequestParam String nsId) {
        ResultModel result = new ResultModel();
        result.setData(unifiedHistoryService.getUnifiedHistory(nsId));
        return ResponseEntity.ok(result);
    }
}
