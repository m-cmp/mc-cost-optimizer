"""
ScoringConfig — 파이프라인 전 단계를 제어하는 단일 설정 객체.

여기 모인 값들이 곧 "튜닝 손잡이"이며, 4단계에서 학습 모델로 넘어갈 때
risk_w_* / *_util 같은 가중치가 '사람이 정한 값' -> '데이터로 학습한 값'으로 대체됩니다.
"""
from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class ScoringConfig:
    # ---------- 수집 / 관측 ----------
    interval_seconds: int = 300       # 수집 주기 (5분=300 ~ 1시간=3600). 우리가 설정.
    window_days: float = 14.0         # 점수 산출에 사용하는 관측 윈도우 길이

    # ---------- 정제 (cleaning) ----------
    max_gap_intervals: int = 3        # 이 길이 이하의 '연속 결측'은 보간, 초과는 결측 유지
    min_coverage: float = 0.80        # 실측 슬롯 비율이 이 미만이면 INSUFFICIENT (점수 보류)
    value_min: float = 0.0            # 사용률 유효 하한(%) — 벗어나면 클립
    value_max: float = 100.0          # 사용률 유효 상한(%)
    warmup_skip_intervals: int = 0    # 부팅/배포 직후 제외할 슬롯 수 (선택, 기본 0)

    # ---------- 20/80 임계 (1단계 규칙 계승) ----------
    low_util: float = 20.0            # 미만 → 과프로비저닝 (다운사이즈 후보)
    high_util: float = 80.0           # 초과 → 과소프로비저닝 (업사이즈 후보)
    target_util: float = 65.0         # 효율 100점 기준점 (이 사용률을 '이상적'으로 봄)

    # ---------- 결정1-c) 비대칭 정책: 업사이즈=공격적 / 다운사이즈=보수적 ----------
    upsize_on_p99: float = 80.0       # P99(피크) ≥ 이 값이면 업사이즈 (공격적: 피크 1발에도 반응)
    downsize_p99_max: float = 50.0    # 다운사이즈하려면 P99(피크)도 이 값 미만이어야 (보수 게이트 ← 핵심 다이얼)

    # ---------- 결정2) 하루 모양(시간대 프로파일) 분석 ----------
    shape_flat_peak_ratio: float = 1.30      # peak/mean 이 미만이면 '평탄 프로파일'
    shape_spiky_ratio: float = 3.0           # (평탄해도) p99/p50 이 이상이면 랜덤 스파이크 → irregular
    shape_concentration_top_n: int = 3       # 집중도 = 상위 N시간 비중
    shape_batch_concentration: float = 0.30  # top-N 비중 이 이상 + 야간피크 → night-batch
    shape_business_start: int = 9
    shape_business_end: int = 18
    shape_night_start: int = 0
    shape_night_end: int = 6

    # ---------- 점수 가중치 (← 향후 학습으로 대체될 자리) ----------
    risk_w_sustained: float = 0.60    # 지속 고부하(P95>high) 가중
    risk_w_peak: float = 0.25         # 순간 피크(P99>high) 가중
    risk_w_busy: float = 0.15         # 고부하 시간 비율 가중
    strong_uptrend_per_day: float = 2.0   # %/day 이상 상승 추세면 다운사이즈 보류

    # ---------- 신뢰도 ----------
    cv_cap: float = 1.0               # 변동계수 정규화 상한 (변동 클수록 신뢰도 ↓)
    min_reliable_days: float = 7.0    # 이 일수 이상이면 window_factor = 1.0
