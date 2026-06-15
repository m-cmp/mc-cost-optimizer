package com.mcmp.ncp.vm.rightsizer.controller;

import com.mcmp.ncp.vm.rightsizer.batch.AsyncExecutorService;
import com.mcmp.ncp.vm.rightsizer.batch.RightSizeType;
import com.mcmp.ncp.vm.rightsizer.client.TumblebugClient;
import com.mcmp.ncp.vm.rightsizer.dto.RecommendCandidateDto;
import com.mcmp.ncp.vm.rightsizer.mapper.ServiceGroupMetaMapper;
import com.mcmp.ncp.vm.rightsizer.mapper.UnusedProcessMartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class NcpBatchController {

    private final AsyncExecutorService batchExecutorService;
    private final TumblebugClient tumblebugClient;
    private final ServiceGroupMetaMapper serviceGroupMetaMapper;
    private final UnusedProcessMartMapper unusedProcessMartMapper;

    @GetMapping(value = "/batch/ncp/recommend")
    public ResponseEntity<String> recommendNcpVm() {
        batchExecutorService.asyncExecuteBatch(RightSizeType.NCP_SIZE_DOWN_VM);
        return ResponseEntity.ok("NCP Recommend VM Type Batch Job started successfully");
    }

    @GetMapping(value = "/batch/ncp/anomaly")
    public ResponseEntity<String> anomalyNcpVm() {
        batchExecutorService.asyncExecuteBatch(RightSizeType.NCP_ANOMALY_VM);
        return ResponseEntity.ok("NCP Anomaly VM Type Batch Job started successfully");
    }

    /**
     * Tumblebug 추천 직접 테스트 (배치 없이 단건 확인)
     *
     * GET /api/ncp/rightsize/test?instanceNo=xxx&direction=up
     *   → servicegroup_meta에서 tbb 식별자 자동 조회
     *
     * GET /api/ncp/rightsize/test?instanceNo=xxx&direction=up&tbbNsId=ns01&tbbMciId=mci01&tbbVmId=vm01
     *   → 파라미터로 tbb 식별자 직접 지정
     */
    @GetMapping(value = "/ncp/rightsize/test")
    public ResponseEntity<Map<String, Object>> testRightSize(
            @RequestParam String instanceNo,
            @RequestParam(defaultValue = "Up") String direction,
            @RequestParam(required = false) String tbbNsId,
            @RequestParam(required = false) String tbbMciId,
            @RequestParam(required = false) String tbbVmId) {

        if (!direction.equalsIgnoreCase("Up") && !direction.equalsIgnoreCase("Down")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "direction은 'Up' 또는 'Down' 만 허용합니다."));
        }

        String normalizedDirection = capitalize(direction);

        // tbb 식별자가 파라미터로 없으면 DB에서 조회
        if (tbbNsId == null || tbbMciId == null || tbbVmId == null) {
            Map<String, String> tbbIds = serviceGroupMetaMapper.selectTbbIdentifiersByInstanceNo(instanceNo);
            if (tbbIds == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "servicegroup_meta에 instanceNo=" + instanceNo + " 에 대한 Tumblebug 식별자가 없습니다.",
                        "hint", "tbbNsId, tbbMciId, tbbVmId 파라미터로 직접 지정하거나 servicegroup_meta에 데이터를 추가하세요."
                ));
            }
            tbbNsId  = tbbIds.get("tbbNsId");
            tbbMciId = tbbIds.get("tbbMciId");
            tbbVmId  = tbbIds.get("tbbVmId");
        }

        RecommendCandidateDto candidate = RecommendCandidateDto.builder()
                .resourceId(instanceNo)
                .recommendType(normalizedDirection)
                .tbbNsId(tbbNsId)
                .tbbMciId(tbbMciId)
                .tbbVmId(tbbVmId)
                .build();

        tumblebugClient.fillCurrentSpec(candidate);

        String recommendSpecName = tumblebugClient.recommendSpec(candidate);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("instanceNo", instanceNo);
        response.put("direction", normalizedDirection);
        response.put("tbbNsId", tbbNsId);
        response.put("tbbMciId", tbbMciId);
        response.put("tbbVmId", tbbVmId);
        response.put("currentSpec", candidate.getCurrentSpecName());
        response.put("currentVcpu", candidate.getCurrentVcpu());
        response.put("currentMemGiB", candidate.getCurrentMemGiB());
        response.put("recommendSpec", recommendSpecName);
        response.put("success", recommendSpecName != null);

        return ResponseEntity.ok(response);
    }

    /**
     * 추천 대상 후보 목록 조회 (배치 실행 전 확인용)
     *
     * GET /api/ncp/rightsize/candidates
     */
    @GetMapping(value = "/ncp/rightsize/candidates")
    public ResponseEntity<Map<String, Object>> getCandidates() {
        List<RecommendCandidateDto> candidates = unusedProcessMartMapper.selectRecommendCandidates();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", candidates.size());
        response.put("candidates", candidates);
        return ResponseEntity.ok(response);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
