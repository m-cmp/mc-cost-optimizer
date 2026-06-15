# LLM 입력 데이터 정의서

> 이 문서는 우리 스코어링 엔진이 LLM에 넘기는 JSON의 **필드 정의 + 해석 규칙**이다.
> LLM 프롬프트에 컨텍스트로 함께 넣어주면, LLM이 각 값의 의미를 알고 추천을 생성한다.

---

## 1. 이 데이터는 무엇인가

가상 컴퓨트(EC2 등) **한 인스턴스**의 일정 기간 CPU/메모리 사용량을 분석한 **자원 적정성 점수**다.
인스턴스마다 독립적으로 계산되며, 다른 인스턴스와 비교/통합하지 않는다.

## 2. ⚠️ 가장 중요한 원칙

- **`action_signal`은 "1차 신호"이지 최종 결정이 아니다.** 최종 추천(다운사이즈 / 유지 / 업사이즈 / 인스턴스 이전 / 삭제)은 **LLM이 결정**한다. 우리는 *신호 + 근거*만 제공한다.
- 우리 정책은 **비대칭**이다: **업사이즈는 공격적**(피크 P99에 민감 — 부족하면 장애), **다운사이즈는 보수적**(P95·P99 둘 다 낮아야). 이 점을 감안해 해석하라.

## 3. 필드 정의

| 필드 | 의미 |
|---|---|
| `instance` | 인스턴스 식별자 |
| `interval_seconds` | 원본 수집 주기(초) |
| `window_days` | 분석에 사용한 기간(일) |
| `coverage` | 데이터 충실도 0~1 (낮으면 신뢰도 낮음) |
| `action_signal` | **1차 신호**: `downsize` / `keep` / `upsize` / `insufficient_data` |
| `efficiency_score` | 0~100, 자원 활용 효율 (**높을수록** 잘 씀 = 낭비 적음). 낮으면 다운사이즈 후보 |
| `headroom_risk` | 0~100, 포화 위험 (**높을수록** 위험). 높으면 업사이즈 후보 |
| `confidence` | 0~1, 이 판정의 신뢰도 (데이터 충실도·안정성·기간 반영) |
| `binding_resource` | `CPU`/`MEM` — 사이징을 좌우하는 더 빡빡한 자원 |
| `reasons` | 판정 근거 문장들 (자연어 설명에 그대로 활용 가능) |
| `metrics.cpu/mem.p95` | 95퍼센타일 사용률 — "거의 항상 이 아래" (지속 부하) |
| `metrics.cpu/mem.p99` | 99퍼센타일 — 드문 피크 |
| `metrics.cpu/mem.mean` | 평균 사용률 |
| `metrics.cpu/mem.idle_ratio` | 한가한(저활용) 시간 비율 0~1 |
| `metrics.trend_per_day` | 사용률 추세 %/day (※ **거친 선형 추정치, 참고용**) |
| `metrics.bound_type` | `cpu-bound`/`mem-bound`/`balanced`/`balanced-idle` |
| `daily_shape.archetype` | 하루 사용 패턴: `flat` / `business-hours` / `night-batch` / `irregular` |
| `daily_shape.peak_window` | 피크가 뜨는 시간대 (예: `02:00–05:00`) |
| `daily_shape.peak_consistency` | 0~1, 매일 **같은 시각**에 피크 뜨는 정도 (높으면 "예약된" 패턴) |
| `daily_shape.concentration` | 0~1, 부하가 소수 시간대에 몰린 정도 |
| `daily_shape.spikiness` | p99/p50, 얼마나 튀는가 |
| `daily_shape.profile_24h` | 24개(0~23시) 시간대별 사용률 곡선 |

## 4. 판정 + 하루 모양(shape) 해석 가이드

`action_signal`을 `daily_shape`와 **함께** 봐야 올바른 추천이 나온다:

| 조합 | LLM 권장 해석 |
|---|---|
| `downsize` + `flat` | 상시 저활용 → **안전한 다운사이즈** |
| `upsize` + `flat` | 하루 종일 포화 → **명확한 상시 업사이즈** |
| `upsize` + `night-batch` (peak_consistency 높음) | 정해진 시간만 피크 → **상시 업사이즈보다 스케줄링/burstable 인스턴스 고려** |
| `upsize` + `irregular` | 예측 불가한 랜덤 피크 → **상시 헤드룸 필요(진짜 업사이즈)** |
| `keep` + 무엇이든 | 적정 사용 → **사이징 변경 추천 안 함** |
| `insufficient_data` | 데이터 부족 → **판단 보류, 추천하지 말 것** |

## 5. 주의사항

- `trend_per_day`는 거친 추정치다. 단독으로 강한 근거로 쓰지 말 것.
- `confidence`가 낮으면(예: < 0.5) 추천 강도를 낮추거나 데이터 더 모으라고 권할 것.
- "modernize(신형 인스턴스 이전)" 같은 액션은 **우리 신호에 없다**. 필요하면 LLM이 비용·세대 정보로 판단할 영역이다.
