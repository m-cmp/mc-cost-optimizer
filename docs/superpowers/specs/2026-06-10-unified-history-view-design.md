# 통합 추천 이력 화면 (Unified Recommendation History) — 설계

- 작성일: 2026-06-10
- 브랜치: `fix/20260610_llm_integration_2`
- 관련 메모리: C2 어휘통일, recommendation-history-view, nsid-tenant-scoping

## 1. 배경 / 목표

Alarm 페이지에는 현재 3개 탭이 있다.

1. **Resource Recommendation** — 추천 요청·실행 UI
2. **Alarm History** — `alarm_history`(기존 ML 리사이저 출력) 표시
3. **Recommendation History** — `recommendation_history`(우리 LLM 출력) 표시

피드백: ② Alarm History와 ③ Recommendation History는 본질이 같은 "자원 리사이징 추천"이라
내용 차이가 작다. 두 화면을 따로 두지 말고 **하나로 합쳐서** 보여주자.

본 설계는 다음 3가지를 다룬다.

- **#6 (핵심)** 두 테이블을 **읽을 때 합쳐(read-time merge)** 한 그리드로 보여준다.
- **#5** 상단 탭을 버튼 → `nav-tabs`(진짜 탭)로. *(이미 구현 완료)*
- **#7** Resource Recommendation 탭의 배지 범례 제거. *(이미 구현 완료)*

비목표(Non-goals): 추천 생성 로직·키 관리·점수 공급(ScoreProvider)은 변경하지 않는다.
기존 알람(`alarm` 모듈, 메일/슬랙)·`AlarmHistoryTable` 컴포넌트는 **수정하지 않는다**(읽기 전용 사용).

## 2. 아키텍처

```
[FE] History 탭 ──GET /history/unified?nsId=──▶ [BE] llm_recommender 모듈 (신규 엔드포인트)
                                                  │
                                                  ├─ alarm_history          (project_cd = nsId, event_type='사이즈 변경'만)
                                                  └─ recommendation_history (ns_id = nsId)
                                                        └─ LEFT JOIN servicegroup_meta (LLM 행 csp 보강)
                                                  │
                                                  ▼ 서비스 계층 병합 → date DESC 정렬
                                          AlarmHistoryTable (기존 7컬럼 그리드 재사용)
```

원칙:

- 신규 엔드포인트는 **우리 모듈(`llm_recommender`)** 에 둔다. 두 테이블 모두 같은 `cost` DB이므로
  읽기만 하면 되고, 알람팀 컨트롤러/서비스는 건드리지 않는다.
- 병합은 **서비스 계층**에서 한다(두 쿼리 → 공통 DTO 매핑 → 합쳐서 정렬).
  단일 거대 SQL UNION보다 `response_json` 파싱·enum 변환·JOIN 보강이 코드로 명확하고 테스트가 쉽다.
  데이터 규모가 작아(LLM 100건 cap) 메모리 병합이 안전하다.

## 3. 통합 그리드 컬럼 매핑

목적지 그리드는 **이미 존재하는** `AlarmHistoryTable`의 7컬럼이다(재사용).

| 그리드 컬럼 | alarm_history (ML) | recommendation_history (LLM) |
|---|---|---|
| `date` | `occure_dt` | `created_at` |
| `csp` | `csp_type` | `servicegroup_meta.csp_type` (JOIN, 없으면 `-`) |
| `resourceId` | `resource_id` | `instance_id` |
| `resourceType` | `resource_type` | `"VM"` (고정·임시) |
| `alarmType` | `"ML"` (상수) | `"LLM"` (상수) |
| `alarmMessage` | `note` | `response_json` 의 `detail` |
| `recommendType` | `plan` → enum 변환(§4) | `recommendation` (그대로) |

- `alarmType`은 컬럼이 아니라 **출처 테이블에서 유도**되는 상수다(alarm_history→ML, recommendation_history→LLM).
- `alarmMessage`는 LLM의 경우 `response_json`을 파싱해 `detail`(한 문장 액션)만 사용한다(`reasoning`은 제외).

## 4. recommendType — 5개 영어 enum 통일

두 테이블이 서로 다른 어휘를 쓰므로, 통합 그리드에서는 **LLM의 5개 영어 enum**으로 통일한다.
LLM enum: `upsize` / `downsize` / `migrate` / `terminate` / `keep`.

ML 어휘 근거(코드):

- `azure-vm-rightsizer/.../dto/RecommendCandidateDto.java`: `recommendType` = `"Up" | "Down" | "Modernize"`
- `azure-vm-rightsizer/.../batch/RecommendVmListItemWriter.java` 주석: `Up(상향), Down(하향), Unused(미사용), Modernize(최신화)`
- `alarm_history.plan = candidate.getRecommendType()` (`RecommendVmListItemProcessor.java`)

매핑:

| ML `plan` | → 통일 enum |
|---|---|
| `Up` | `upsize` |
| `Down` | `downsize` |
| `Modernize` | `migrate` |
| `Unused` | `terminate` |
| (해당 없음) | `keep` ← LLM 전용 (ML은 "유지"를 알람으로 보내지 않음) |

주의: `plan` 저장값이 영어(`Up`)인지 한글(`상향`)인지 코드와 목업이 엇갈린다(§7-3).
매핑 함수는 두 표기를 모두 정규화하도록 작성하고, 운영 실값으로 확정한다.

## 5. 필터 / 정렬

- 파라미터 1개 **`nsId`**(= 프론트 `useProjectStore().projectId`).
  - alarm_history: `WHERE project_cd = #{nsId}`
  - recommendation_history: `WHERE ns_id = #{nsId}`
  - 두 테이블 모두 같은 `projectId` 값으로 필터된다(기존 동작과 동일).
- **비용 경고 행 제외**: alarm_history는 `event_type = '사이즈 변경'` 행만 포함한다.
  비용 급증/비정상 경고(`event_type='비정상'` 등)는 리사이징 추천이 아니므로 통합 화면에서 제외한다.
- 정렬: `date DESC`. 페이징: 클라이언트 `pageSize 10`(기존 `AlarmHistoryTable` 그대로).

## 6. 프론트엔드 변경

- `cost-fe/src/pages/alarm/AlarmPage.jsx`
  - "Recommendation History" 탭 제거 → **2탭**(Resource Recommendation / History).
  - "History" 탭이 신규 훅으로 `/history/unified` 호출 → 결과를 기존 `AlarmHistoryTable`에 전달.
- 신규: `useUnifiedHistory` 훅 + `getUnifiedHistory(nsId)` API 클라이언트.
- `RecommendHistoryTab.jsx` 미사용 → 제거.
- 기존 `AlarmHistoryTable.jsx`는 무수정(그리드 7컬럼이 이미 일치).
- *(완료됨)* `#5` nav-tabs 전환, `#7` 배지 범례 제거.

## 7. 확인 / 의존 사항 (구현 전 정리)

1. **resourceType 출처** — `servicegroup_meta`에 `resource_type` 컬럼이 없다(VM 메타만).
   현재 설계는 LLM 행을 `"VM"`으로 고정한다. 피드백 원안("자원 목록 테이블에 있음")과 어긋나므로,
   원래 의도(다른 자원 테이블? 컬럼 추가?)를 사수에게 확인 권장.
2. **`project_cd = nsId` 실증** — 코드 흐름상 같은 값이나 운영 데이터로 한 번 확인.
3. **`plan` 저장값 표기** — 코드는 영어(`Up`), cost-fe 목업은 한글(`상향`). 운영 실값 확인 후 매핑 분기 확정.
4. **데모 인스턴스 csp** — 현재 LLM 추천은 데모 인스턴스(`i-demo-*`)라 `servicegroup_meta` JOIN이
   비어 `csp=-`로 나온다. 실데이터(A1 인스턴스 목록) 연동 시 해소.
5. **비용 경고 화면 손실** — 통합 후 비용 경고는 이 화면에서 빠진다(메일/슬랙에서만 노출).
   팀 공유 필요.

## 8. 테스트 전략

- **백엔드 단위테스트**(병합 서비스):
  - ML만 / LLM만 / 둘 다 / 빈 결과
  - enum 변환(Up→upsize 등), 알 수 없는 plan 처리
  - 비용 경고 행 제외(`event_type` 필터)
  - servicegroup_meta JOIN 누락 시 `csp=-`
  - `response_json` 파싱 실패 시 `alarmMessage` 안전 처리
- **프론트**: 통합 History 탭 렌더 + 빌드/lint.
- **E2E**: 운영 DB(<OPERATIONAL_DB_HOST:PORT>)에서 `GET /history/unified?nsId=` 실호출, 정렬·필터 확인.

## 9. 산출물 / 엔드포인트 요약

- 신규: `GET /api/costopti/be/llm_recommender/history/unified?nsId=<ns>`
  - 응답: `ResultModel.Data = UnifiedHistoryRow[]` (7컬럼)
- 신규 백엔드: 병합 서비스 메서드 + 매퍼 2개 쿼리(alarm 측 추천행, rec 측 JOIN) + DTO + enum 매핑 유틸
- 신규 프론트: `getUnifiedHistory` API · `useUnifiedHistory` 훅 · AlarmPage 탭 정리
