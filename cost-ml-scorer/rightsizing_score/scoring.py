"""
STAGE 3 — 점수 산출 ("모델").

지금은 RuleScorer = 사람이 정의한 결정론적 스코어링 함수.
50대 규모에서는 분류/회귀 ML이 과적합되므로, 해석 가능한 규칙 점수가 가장 견고하다.

* Scorer 는 추상 인터페이스 → 4단계에서 LearnedScorer(LightGBM 등)가
  같은 score(fv) -> InstanceScore 시그니처로 '드롭인 교체'된다.
* 즉 config 의 가중치를 '사람이 정한 값' → '데이터로 학습한 값'으로 바꾸는 게 ML 전환.
"""
from __future__ import annotations

from abc import ABC, abstractmethod
from typing import List

from .config import ScoringConfig
from .models import Action, FeatureVector, InstanceScore


def _clamp(x: float, lo: float, hi: float) -> float:
    return max(lo, min(hi, x))


class Scorer(ABC):
    @abstractmethod
    def score(self, fv: FeatureVector) -> InstanceScore:
        ...


class RuleScorer(Scorer):
    def __init__(self, config: ScoringConfig):
        self.c = config

    def score(self, fv: FeatureVector) -> InstanceScore:
        c = self.c
        bp95, bp99, busy = fv.binding_p95, fv.binding_p99, fv.binding_busy_ratio

        # --- (1) 효율 점수: binding 사용률을 target 대비로 본다 (낮을수록 낭비) ---
        efficiency = int(round(_clamp(100.0 * bp95 / c.target_util, 0.0, 100.0)))

        # --- (2) 헤드룸 리스크: 천장에 얼마나 가까운가 (지속/순간/빈도 가중합) ---
        sustained = _clamp((bp95 - c.high_util) / (100.0 - c.high_util), 0.0, 1.0)
        peak = _clamp((bp99 - c.high_util) / (100.0 - c.high_util), 0.0, 1.0)
        risk = 100.0 * _clamp(
            c.risk_w_sustained * sustained + c.risk_w_peak * peak + c.risk_w_busy * busy,
            0.0, 1.0,
        )
        headroom_risk = int(round(risk))

        # --- (3) 액션 결정 — 결정1-c) 비대칭 정책 (업사이즈 공격적 / 다운사이즈 보수적) ---
        reasons: List[str] = []
        strong_up = fv.trend_per_day >= c.strong_uptrend_per_day

        if bp99 >= c.upsize_on_p99 or bp95 >= c.high_util:
            # 업사이즈(공격적): 피크(P99)에 민감 — under-provision = 장애 위험
            action = Action.UPSIZE
            reasons.append(
                f"{fv.binding_resource} 피크 P99 {bp99:.0f}% / 지속 P95 {bp95:.0f}% — 포화 위험(공격적 업사이즈)"
            )
            if busy > 0:
                reasons.append(f"고부하(>{c.high_util:.0f}%) 시간 비율 {busy:.0%}")
        elif bp95 < c.low_util and bp99 < c.downsize_p99_max and not strong_up:
            # 다운사이즈(보수적): 지속(P95)도 낮고 피크(P99)까지 낮아야만
            action = Action.DOWNSIZE
            reasons.append(
                f"지속 P95 {bp95:.0f}% < {c.low_util:.0f}% & 피크 P99 {bp99:.0f}% < {c.downsize_p99_max:.0f}% — 안전 다운사이즈"
            )
            if fv.mem is not None:
                reasons.append(f"유휴 비율 CPU {fv.cpu.idle_ratio:.0%} / MEM {fv.mem.idle_ratio:.0%}")
            else:
                reasons.append(f"유휴 비율 CPU {fv.cpu.idle_ratio:.0%}")
        else:
            action = Action.KEEP
            if bp95 < c.low_util and bp99 >= c.downsize_p99_max:
                reasons.append(
                    f"지속은 저활용이나 피크 P99 {bp99:.0f}% ≥ {c.downsize_p99_max:.0f}% — 보수적 유지(다운사이즈 보류)"
                )
            elif strong_up and bp95 < c.low_util:
                reasons.append(
                    f"상승 추세 {fv.trend_per_day:+.1f}%/day → 다운사이즈 보류"
                )
            else:
                reasons.append(f"{fv.binding_resource} P95 {bp95:.0f}% — 적정 구간")

        # --- (4) 신뢰도: 커버리지 × 안정성 × 윈도우 충분도 ---
        cov_factor = fv.coverage
        stability = _clamp(1.0 - fv.binding_cv / c.cv_cap, 0.3, 1.0)
        window_factor = _clamp(fv.window_days / c.min_reliable_days, 0.0, 1.0)
        confidence = round(cov_factor * stability * window_factor, 2)

        return InstanceScore(
            instance_id=fv.instance_id,
            efficiency_score=efficiency,
            headroom_risk=headroom_risk,
            action=action,
            confidence=confidence,
            binding_resource=fv.binding_resource,
            reasons=reasons,
        )
