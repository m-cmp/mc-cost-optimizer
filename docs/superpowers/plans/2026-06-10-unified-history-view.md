# 통합 추천 이력 화면 (Unified Recommendation History) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `alarm_history`(ML 추천)와 `recommendation_history`(LLM 추천)를 읽을 때 합쳐 한 그리드(`GET /history/unified`)로 내려주고, 프론트 History 탭 하나로 보여준다.

**Architecture:** 우리 모듈(`llm_recommender`)에 신규 읽기 엔드포인트를 추가한다. 매퍼가 두 테이블을 각각 조회(알람측=추천행만, LLM측=servicegroup_meta LEFT JOIN)하고, 서비스 계층에서 ML `plan`→5개 enum 변환·LLM `response_json`→`detail` 추출·`date DESC` 병합을 한다. 알람팀 기존 코드는 읽기만 하고 수정하지 않는다.

**Tech Stack:** Spring Boot 2.7 / MyBatis(`sqlSessionTemplateBill`, `cost` DB) / JUnit5 + AssertJ + ReflectionTestUtils / React + Vite(cost-fe).

**Branch:** `fix/20260610_llm_integration_2`

**Spec:** `docs/superpowers/specs/2026-06-10-unified-history-view-design.md`

**선행 완료(이미 구현됨, 본 계획에선 마지막에 함께 커밋):** #5 nav-tabs(`AlarmPage.jsx`), #7 배지 범례 제거(`RecommendTab.jsx`).

---

## File Structure

**백엔드 (신규)**
- `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/model/UnifiedHistoryRow.java` — 통합 그리드 1행 DTO(7필드 + transient responseJson)
- `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/service/RecommendTypeMapper.java` — ML `plan`(Up/Down/Modernize/Unused, 영/한) → 5개 enum
- `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/dao/UnifiedHistoryDao.java` — 두 SELECT 호출
- `BackEnd/src/main/resources/mapper/bill/unified_history_SQL.xml` — alarm측/LLM측 쿼리
- `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/service/UnifiedHistoryService.java` — 병합/변환/정렬

**백엔드 (수정)**
- `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/LlmRecommendController.java` — `GET /history/unified` 추가

**백엔드 (테스트, 신규)**
- `BackEnd/src/test/java/com/mcmp/costbe/llm_recommender/service/RecommendTypeMapperTest.java`
- `BackEnd/src/test/java/com/mcmp/costbe/llm_recommender/service/UnifiedHistoryServiceTest.java`

**프론트 (수정/신규)**
- `cost-fe/src/api/llm_recommender/llmRecommender.js` — `getUnifiedHistory` 추가
- `cost-fe/src/config/mockData.js` — `mockUnifiedHistory` 추가
- `cost-fe/src/hooks/useUnifiedHistory.js` — 신규 훅
- `cost-fe/src/pages/alarm/AlarmPage.jsx` — 탭 2개로 정리, History 탭이 통합 데이터 사용
- 삭제: `cost-fe/src/pages/alarm/components/recommend/RecommendHistoryTab.jsx`

---

## Task 1: UnifiedHistoryRow DTO

**Files:**
- Create: `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/model/UnifiedHistoryRow.java`

- [ ] **Step 1: 모델 생성**

```java
package com.mcmp.costbe.llm_recommender.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * One row of the unified recommendation-history grid (AlarmHistoryTable 7 columns).
 * Populated from either alarm_history (ML) or recommendation_history (LLM).
 */
@Getter
@Setter
public class UnifiedHistoryRow {

    private String date;          // 'yyyy-MM-dd HH:mm:ss' (string, sortable)
    private String csp;
    private String resourceId;
    private String resourceType;
    private String alarmType;     // "ML" | "LLM"
    private String alarmMessage;
    private String recommendType; // upsize|downsize|migrate|terminate|keep

    /** LLM rows only: raw response_json, used to derive alarmMessage; never serialized. */
    @JsonIgnore
    private String responseJson;
}
```

- [ ] **Step 2: 컴파일 확인**

Run (from `BackEnd`): `export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" && ./mvnw.cmd -q -DskipTests compile`
Expected: BUILD SUCCESS (출력 없음).

- [ ] **Step 3: 커밋**

```bash
git add BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/model/UnifiedHistoryRow.java
git commit -m "feat(llm): add UnifiedHistoryRow DTO for merged history grid"
```

---

## Task 2: RecommendTypeMapper (TDD)

ML `plan` 값을 LLM 5-enum으로 변환. 영어(`Up`)·한글(`상향`) 모두 처리, 미지값은 원문 통과.

**Files:**
- Create: `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/service/RecommendTypeMapper.java`
- Test: `BackEnd/src/test/java/com/mcmp/costbe/llm_recommender/service/RecommendTypeMapperTest.java`

- [ ] **Step 1: 실패 테스트 작성**

```java
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
```

- [ ] **Step 2: 실패 확인**

Run (from `BackEnd`): `export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" && ./mvnw.cmd -q -Dtest=RecommendTypeMapperTest test`
Expected: FAIL — `RecommendTypeMapper` 클래스 없음(compile error).

- [ ] **Step 3: 구현**

```java
package com.mcmp.costbe.llm_recommender.service;

import org.springframework.stereotype.Component;

/** Maps the ML rightsizer's plan vocabulary to the LLM 5-enum (upsize/downsize/migrate/terminate/keep). */
@Component
public class RecommendTypeMapper {

    public String toEnum(String plan) {
        if (plan == null) return "-";
        switch (plan.trim().toLowerCase()) {
            case "up":
            case "상향":
                return "upsize";
            case "down":
            case "하향":
                return "downsize";
            case "modernize":
            case "최신화":
                return "migrate";
            case "unused":
            case "미사용":
                return "terminate";
            default:
                return plan; // unknown value passes through unchanged
        }
    }
}
```

- [ ] **Step 4: 통과 확인**

Run: `export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" && ./mvnw.cmd -q -Dtest=RecommendTypeMapperTest test`
Expected: PASS (Tests run: 4, Failures: 0).

- [ ] **Step 5: 커밋**

```bash
git add BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/service/RecommendTypeMapper.java BackEnd/src/test/java/com/mcmp/costbe/llm_recommender/service/RecommendTypeMapperTest.java
git commit -m "feat(llm): add RecommendTypeMapper (ML plan -> 5-enum)"
```

---

## Task 3: DAO + 매퍼 SQL

두 쿼리: 알람측(추천행만, 상수 'ML' 태깅) / LLM측(servicegroup_meta LEFT JOIN, 상수 'LLM'/'VM').

**Files:**
- Create: `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/dao/UnifiedHistoryDao.java`
- Create: `BackEnd/src/main/resources/mapper/bill/unified_history_SQL.xml`

- [ ] **Step 1: DAO 생성**

```java
package com.mcmp.costbe.llm_recommender.dao;

import com.mcmp.costbe.llm_recommender.model.UnifiedHistoryRow;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class UnifiedHistoryDao {

    @Resource(name = "sqlSessionTemplateBill")
    private SqlSessionTemplate sqlSession;

    public List<UnifiedHistoryRow> selectAlarmRecommendations(Map<String, Object> params) {
        return sqlSession.selectList("unifiedHistory.selectAlarmRecommendations", params);
    }

    public List<UnifiedHistoryRow> selectLlmRecommendations(Map<String, Object> params) {
        return sqlSession.selectList("unifiedHistory.selectLlmRecommendations", params);
    }
}
```

- [ ] **Step 2: 매퍼 XML 생성**

`date`는 예약어이므로 백틱 처리. csp는 JOIN 누락 시 `-`(COALESCE). 알람측은 `event_type='사이즈 변경'`(추천행)만.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="unifiedHistory">

    <!-- ML side: alarm_history, resize-recommendation rows only (cost-warning rows excluded). -->
    <select id="selectAlarmRecommendations" parameterType="map"
            resultType="com.mcmp.costbe.llm_recommender.model.UnifiedHistoryRow">
        SELECT DATE_FORMAT(occure_dt, '%Y-%m-%d %H:%i:%s') AS `date`,
               csp_type      AS csp,
               resource_id   AS resourceId,
               resource_type AS resourceType,
               'ML'          AS alarmType,
               note          AS alarmMessage,
               plan          AS recommendType
          FROM alarm_history
         WHERE project_cd = #{nsId}
           AND event_type = '사이즈 변경'
         ORDER BY occure_dt DESC
         LIMIT #{limit}
    </select>

    <!-- LLM side: recommendation_history JOIN servicegroup_meta for csp. -->
    <select id="selectLlmRecommendations" parameterType="map"
            resultType="com.mcmp.costbe.llm_recommender.model.UnifiedHistoryRow">
        SELECT DATE_FORMAT(rh.created_at, '%Y-%m-%d %H:%i:%s') AS `date`,
               COALESCE(sgm.csp_type, '-') AS csp,
               rh.instance_id   AS resourceId,
               'VM'             AS resourceType,
               'LLM'            AS alarmType,
               rh.recommendation AS recommendType,
               rh.response_json  AS responseJson
          FROM recommendation_history rh
          LEFT JOIN servicegroup_meta sgm ON rh.instance_id = sgm.csp_instanceid
         WHERE rh.ns_id = #{nsId}
         ORDER BY rh.created_at DESC
         LIMIT #{limit}
    </select>

</mapper>
```

- [ ] **Step 3: 컴파일 확인**

Run (from `BackEnd`): `export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" && ./mvnw.cmd -q -DskipTests compile`
Expected: BUILD SUCCESS.

- [ ] **Step 4: 커밋**

```bash
git add BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/dao/UnifiedHistoryDao.java BackEnd/src/main/resources/mapper/bill/unified_history_SQL.xml
git commit -m "feat(llm): add UnifiedHistoryDao + merge SQL (alarm + recommendation)"
```

---

## Task 4: UnifiedHistoryService (TDD)

병합 핵심: ML 행 `recommendType` enum 변환, LLM 행 `responseJson`→`detail` 추출, `date DESC` 정렬, 100건 cap.

**Files:**
- Create: `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/service/UnifiedHistoryService.java`
- Test: `BackEnd/src/test/java/com/mcmp/costbe/llm_recommender/service/UnifiedHistoryServiceTest.java`

- [ ] **Step 1: 실패 테스트 작성**

```java
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
```

- [ ] **Step 2: 실패 확인**

Run: `export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" && ./mvnw.cmd -q -Dtest=UnifiedHistoryServiceTest test`
Expected: FAIL — `UnifiedHistoryService` 없음(compile error).

- [ ] **Step 3: 구현**

```java
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
```

- [ ] **Step 4: 통과 확인**

Run: `export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" && ./mvnw.cmd -q -Dtest=UnifiedHistoryServiceTest test`
Expected: PASS (Tests run: 5, Failures: 0).

- [ ] **Step 5: 커밋**

```bash
git add BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/service/UnifiedHistoryService.java BackEnd/src/test/java/com/mcmp/costbe/llm_recommender/service/UnifiedHistoryServiceTest.java
git commit -m "feat(llm): add UnifiedHistoryService (merge ML+LLM history)"
```

---

## Task 5: 컨트롤러 엔드포인트

**Files:**
- Modify: `BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/LlmRecommendController.java`

- [ ] **Step 1: 서비스 주입 필드 추가**

`@Autowired private LlmModelProperties modelProperties;` 아래(line 28 부근)에 추가:

```java
    @Autowired
    private com.mcmp.costbe.llm_recommender.service.UnifiedHistoryService unifiedHistoryService;
```

- [ ] **Step 2: 엔드포인트 추가**

기존 `history(...)` 메서드(닫는 `}` line 59 부근) 다음, 컨트롤러 클래스 닫기 전에 추가:

```java
    @GetMapping(path = "/history/unified")
    @Operation(summary = "통합 추천 이력 조회",
            description = "alarm_history(ML)와 recommendation_history(LLM)를 합쳐 nsId별로 최신순 반환한다.")
    public ResponseEntity unifiedHistory(@RequestParam String nsId) {
        ResultModel result = new ResultModel();
        result.setData(unifiedHistoryService.getUnifiedHistory(nsId));
        return ResponseEntity.ok(result);
    }
```

- [ ] **Step 3: 전체 백엔드 테스트 + 컴파일**

Run (from `BackEnd`): `export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" && ./mvnw.cmd -q test`
Expected: BUILD SUCCESS, 모든 테스트 green(기존 + RecommendTypeMapperTest + UnifiedHistoryServiceTest).

- [ ] **Step 4: 커밋**

```bash
git add BackEnd/src/main/java/com/mcmp/costbe/llm_recommender/LlmRecommendController.java
git commit -m "feat(llm): add GET /history/unified endpoint"
```

---

## Task 6: 프론트 API + 훅 + mock

**Files:**
- Modify: `cost-fe/src/api/llm_recommender/llmRecommender.js`
- Modify: `cost-fe/src/config/mockData.js`
- Create: `cost-fe/src/hooks/useUnifiedHistory.js`

- [ ] **Step 1: mock 데이터 추가**

`cost-fe/src/config/mockData.js` 맨 끝에 추가(통합 그리드 7컬럼 형태):

```js
// Unified history mock (ML + LLM rows in the 7-column grid shape).
export const mockUnifiedHistory = [
  { date: "2026-06-10 14:21:03", csp: "AWS", resourceId: "i-0abc123", resourceType: "AmazonEC2",
    alarmType: "ML", alarmMessage: "기존 타입 t3.large에서 t3.medium으로 변경 추천", recommendType: "downsize" },
  { date: "2026-06-10 11:02:55", csp: "AZURE", resourceId: "i-demo-keep", resourceType: "VM",
    alarmType: "LLM", alarmMessage: "Keep the current size.", recommendType: "keep" },
  { date: "2026-06-09 19:40:10", csp: "-", resourceId: "i-demo-upsize", resourceType: "VM",
    alarmType: "LLM", alarmMessage: "Step up one size (t3.xlarge).", recommendType: "upsize" },
];
```

- [ ] **Step 2: API 함수 추가**

`cost-fe/src/api/llm_recommender/llmRecommender.js`:
- 상단 import 줄 수정:
```js
import { mockRecommendation, mockRecommendHistory, mockUnifiedHistory } from "../../config/mockData";
```
- 파일 끝에 추가:
```js
// Fetch the unified history (ML alarm recs + LLM recs) for a namespace.
export const getUnifiedHistory = (nsId) => {
  if (USE_MOCK) return Promise.resolve({ data: { Data: mockUnifiedHistory } });
  return llmClient.get("/history/unified", { params: { nsId } });
};
```

- [ ] **Step 3: 훅 생성**

`cost-fe/src/hooks/useUnifiedHistory.js`:

```js
import { useState, useEffect } from "react";
import { getUnifiedHistory } from "@/api/llm_recommender/llmRecommender";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @hook useUnifiedHistory
 * @description Fetches the unified recommendation history (ML + LLM) for the
 *   current namespace (projectId). Waits for projectId (null guard).
 */
export function useUnifiedHistory() {
  const projectId = useProjectStore((s) => s.projectId);
  const { addAlert } = useAlertStore();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!projectId) return; // nsId 준비 전엔 호출 보류
    const fetchHistory = async () => {
      setLoading(true);
      try {
        const res = await getUnifiedHistory(projectId);
        setHistory(res.data.Data || []);
      } catch (err) {
        logger.error("Unified history error:", err);
        addAlert({
          variant: "danger",
          title: "API Error",
          message: err.userMessage || "Failed to load recommendation history.",
        });
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, [projectId, addAlert]);

  return { history, loading };
}
```

- [ ] **Step 4: lint 확인**

Run (from `cost-fe`): `npx eslint src/api/llm_recommender/llmRecommender.js src/hooks/useUnifiedHistory.js src/config/mockData.js`
Expected: exit 0(경고 없음).

- [ ] **Step 5: 커밋**

```bash
git add cost-fe/src/api/llm_recommender/llmRecommender.js cost-fe/src/hooks/useUnifiedHistory.js cost-fe/src/config/mockData.js
git commit -m "feat(cost-fe): add getUnifiedHistory API + useUnifiedHistory hook"
```

---

## Task 7: AlarmPage 탭 통합 + RecommendHistoryTab 제거

3탭 → 2탭(Resource Recommendation / History). History 탭이 통합 데이터를 `AlarmHistoryTable`에 전달. 메일/슬랙 버튼은 유지.

**Files:**
- Modify: `cost-fe/src/pages/alarm/AlarmPage.jsx`
- Delete: `cost-fe/src/pages/alarm/components/recommend/RecommendHistoryTab.jsx`

- [ ] **Step 1: AlarmPage.jsx 전체 교체**

```jsx
import { useState } from "react";
import AlarmHistoryTable from "./components/AlarmHistoryTable";
import MailingGuideModal from "./components/modals/MailingGuideModal";
import SlackGuideModal from "./components/modals/SlackGuideModal";
import MailTestModal from "./components/modals/MailTestModal";
import SlackTestButton from "./components/SlackTestButton";
import RecommendTab from "./components/recommend/RecommendTab";
import Loading from "@/components/common/loading/Loading";
import { useUnifiedHistory } from "@/hooks/useUnifiedHistory";

export default function AlarmPage() {
  const { history, loading } = useUnifiedHistory();
  const [tab, setTab] = useState("recommend"); // "recommend" | "history"

  return (
    <div>
      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <a
            className={`nav-link ${tab === "recommend" ? "active" : ""}`}
            role="button"
            onClick={() => setTab("recommend")}
          >
            Resource Recommendation
          </a>
        </li>
        <li className="nav-item">
          <a
            className={`nav-link ${tab === "history" ? "active" : ""}`}
            role="button"
            onClick={() => setTab("history")}
          >
            History
          </a>
        </li>
      </ul>

      {tab === "recommend" && <RecommendTab />}

      {tab === "history" && (
        loading ? (
          <Loading fullscreen withLabel label="Loading data..." />
        ) : (
          <div>
            <div className="d-flex gap-3 mb-3">
              <MailingGuideModal />
              <SlackGuideModal />
              <MailTestModal />
              <SlackTestButton />
            </div>
            <AlarmHistoryTable data={history} />
          </div>
        )
      )}
    </div>
  );
}
```

- [ ] **Step 2: RecommendHistoryTab 삭제**

```bash
git rm cost-fe/src/pages/alarm/components/recommend/RecommendHistoryTab.jsx
```

- [ ] **Step 3: lint + 빌드 확인**

Run (from `cost-fe`):
`npx eslint src/pages/alarm/AlarmPage.jsx` → exit 0
`npm run build` → 빌드 성공(에러 없음). `RecommendHistoryTab`/`useRecommendHistory`/`useAlarmHistory` 참조 잔존 시 빌드가 실패하므로 여기서 드러난다.
Expected: 둘 다 성공.

- [ ] **Step 4: 커밋**

```bash
git add cost-fe/src/pages/alarm/AlarmPage.jsx
git commit -m "feat(cost-fe): merge history tabs into one History tab (unified)"
```

---

## Task 8: #5/#7 선행 변경 커밋 + 최종 검증

#5(nav-tabs는 Task 7에서 최종형으로 흡수됨)·#7(배지 범례 제거)은 working tree에 이미 적용됨. `RecommendTab.jsx` 변경을 커밋한다.

**Files:**
- Modify(이미 적용됨): `cost-fe/src/pages/alarm/components/recommend/RecommendTab.jsx`

- [ ] **Step 1: 잔여 변경 확인**

Run (repo 루트): `git status -s`
Expected: `RecommendTab.jsx`(M)만 남아 있어야 함(나머지는 Task별 커밋 완료). `docs/llm-recommender.md`(??)는 그대로 두고 **절대 add 금지**.

- [ ] **Step 2: #7 커밋**

```bash
git add cost-fe/src/pages/alarm/components/recommend/RecommendTab.jsx
git commit -m "feat(cost-fe): remove badge legend from Resource Recommendation tab"
```

- [ ] **Step 3: 최종 전체 검증**

```bash
# backend
cd BackEnd && export JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-17.0.19.10-hotspot" && ./mvnw.cmd -q test && cd ..
# frontend
cd cost-fe && npm run build && cd ..
```
Expected: 백엔드 전체 테스트 green, 프론트 빌드 성공.

- [ ] **Step 4: 최종 상태 확인**

Run: `git status -s`
Expected: `docs/llm-recommender.md`(??)만 남음(untracked, 의도된 미커밋). 그 외 깨끗.

---

## E2E 검증 (수동, 선택)

- 운영 DB(`<OPERATIONAL_DB_HOST:PORT>`)로 jdbc-url 임시 변경 + `LLM_KEY_MASTER` 주입하여 백엔드 기동(검증 후 원복, 커밋 금지).
- `GET http://localhost:9090/api/costopti/be/llm_recommender/history/unified?nsId=default` → 200 + 7컬럼 배열.
- 프론트(LLM 라이브 임시 플래그) → Alarm → History 탭에서 ML/LLM 행이 한 표에 최신순으로 보이는지 확인.

---

## Self-Review (작성자 점검 결과)

- **스펙 커버리지:** §2 아키텍처=Task3~5 / §3 컬럼매핑=Task3(SQL)+Task4(detail·enum) / §4 enum 통일=Task2 / §5 필터·정렬·경고제외=Task3(WHERE)+Task4(sort) / §6 프론트=Task6~7 / §8 테스트=Task2,4 단위 + Task8 빌드 + E2E. 누락 없음.
- **placeholder:** 없음(모든 코드·명령·기대출력 명시). `"VM"`·`"-"`는 스펙에서 확정된 값.
- **타입 일관성:** `UnifiedHistoryRow`(7필드+responseJson) / `RecommendTypeMapper.toEnum` / `UnifiedHistoryService.getUnifiedHistory` / DAO `selectAlarmRecommendations`·`selectLlmRecommendations` / 매퍼 namespace `unifiedHistory` — Task 간 시그니처·이름 일치 확인.
- **확인사항(스펙 §7):** plan 영/한은 매퍼가 둘 다 처리(Task2). resourceType `"VM"`·데모 csp `"-"`는 의도된 임시. project_cd=nsId·운영 plan 실값은 E2E에서 실증.
