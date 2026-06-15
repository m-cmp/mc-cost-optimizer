# ml-rightsize — AWS VM Rightsizer (CPU-only)

가상 컴퓨트(EC2 등)의 CPU 사용률을 분석해 **자원 적정성 점수**를 산출하는 **stateless API**.
인스턴스 ID를 받으면 DB에서 최근 ~30일 CPU를 읽어 점수 JSON을 돌려준다.
(이 점수 JSON을 LLM 추천 엔진이 받아 최종 추천을 생성한다 — LLM 측은 별도 모듈)

## 특징
- Python (3.9+), 의존성 최소 — **PyMySQL** 하나
- **Stateless** — 적재 없음, 요청당 계산 후 응답
- **CPU-only** — 현재 `cpu` 메트릭만 수집됨 (메모리 없음 → 출력에 `mem` 없음)
- 수집 간격 **자동 추론**, 분석 기간 기본 30일

---

## 빠른 시작

### 1) 로컬 실행
```bash
pip install -r requirements.txt
export COST_DB_URL="jdbc:mariadb://HOST:PORT/cost?useSSL=false"
export COST_DB_USERNM="<user>"
export COST_DB_PW="<pw>"
python3 -m rightsizing_score.api          # 기본 0.0.0.0:8093
```

### 2) Docker
```bash
docker build -t ml-rightsize .
docker run --rm -p 8093:8093 \
  -e COST_DB_URL="jdbc:mariadb://host.docker.internal:3306/cost?useSSL=false" \
  -e COST_DB_USERNM="<user>" -e COST_DB_PW="<pw>" \
  ml-rightsize
```

### 3) docker-compose (기존 `.env` 재사용)
```yaml
  ml-rightsizer:
    build: .
    networks: [mcmp_cost_network]
    ports: ["8093:8093"]
    environment:
      COST_DB_URL: ${COST_DB_URL}
      COST_DB_USERNM: ${COST_DB_USERNM}
      COST_DB_PW: ${COST_DB_PW}
      # SERVER_PORT: "8093"     # 선택
      # WINDOW_DAYS: "30"       # 선택
```

---

## 환경변수

| 변수 | 필수 | 기본 | 설명 |
|---|---|---|---|
| `COST_DB_URL` | ✓ | — | JDBC URL(mariadb). 파싱해서 host/port/db 추출 |
| `COST_DB_USERNM` | ✓ | — | DB 유저 |
| `COST_DB_PW` | ✓ | — | DB 비번 |
| `SERVER_PORT` | | `8093` | API 포트 |
| `WINDOW_DAYS` | | `30` | 분석 기간(일) |

## API

**`POST /ml-rightsize`**
```jsonc
요청:  { "csp_instanceid": "i-0233..." }
응답:  {
  "instance": "...", "action_signal": "keep|downsize|upsize|insufficient_data",
  "efficiency_score": 0-100, "headroom_risk": 0-100, "confidence": 0-1,
  "binding_resource": "CPU",
  "reasons": [ ... ],
  "metrics": { "cpu": {p95,p99,mean,idle_ratio}, "trend_per_day": .., "bound_type": "cpu-only" },
  "daily_shape": { "archetype": .., "peak_window": .., "profile_24h": [..] }
}
```
**`GET /health`** → `{"status":"ok"}`

- 여러 인스턴스는 **호출 측에서 for 루프** (API는 인스턴스 1개 단위)
- 응답 필드 의미: **`LLM_DATA_GUIDE.md`**

## DB 의존

읽기 전용 — `cost.asset_compute_metric` 의 `metric_type='cpu'` 행만 사용.
매핑: instance=`csp_instanceid`, ts=`collect_dt`, cpu=`metric_amount`.

---

## 프로젝트 구조
```
rightsizing_score/
  api.py         ← HTTP 엔트리 (POST /ml-rightsize)
  datasource.py  ← DB 어댑터(AssetComputeMetricSource) + 수집간격 자동추론
  cleaning.py    ← STAGE1 정제 (그리드/결측/이상치/커버리지)
  features.py    ← STAGE2 피처 (P95/P99/idle/binding/trend)
  shape.py       ← 하루 모양(시간대 프로파일) 분석
  scoring.py     ← STAGE3 규칙 점수 (비대칭 정책)
  pipeline.py    ← 단계 오케스트레이션
  explain.py     ← to_llm_context (LLM 입력 JSON 생성)
  models.py / config.py
  demo.py        ← 합성 데이터 테스트 하네스
Dockerfile, requirements.txt
LLM_DATA_GUIDE.md   ← 출력 JSON 필드 정의 (LLM 프롬프트 동봉용)
LLM_DEV_SPEC.html   ← LLM 연동 측 개발 정의서
```

## 점수 로직 요약
- 백분위수(P95/P99) 기반, 최근 ~30일
- **비대칭 정책**: 업사이즈 공격적(P99 피크 민감) / 다운사이즈 보수적(P95·P99 둘 다 낮아야)
- `confidence` = 커버리지 × 안정성 × 기간충분도 (데이터 짧으면 낮게)
- 튜닝 손잡이: `config.py` 의 `ScoringConfig`

## 테스트
```bash
python3 -m rightsizing_score.demo          # 합성 데이터로 전 단계 동작 검증
```

## 알려진 한계 / TODO
- CPU-only (메모리 미수집) — 메모리 병목 워크로드는 못 잡음
- `trend_per_day` 는 거친 선형추정 (참고용, 강한 근거로 쓰지 말 것)
- `spikiness` 가 완전 idle(p50≈0)에서 큰 값 → 보정 여지
- 실데이터 충분히 쌓이면 임계값(특히 `downsize_p99_max`) 튜닝 권장
