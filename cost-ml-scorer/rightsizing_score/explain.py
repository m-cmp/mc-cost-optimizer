"""
가시화 계층 — "어떻게 정제 → 어떤 수치로 → 어떤 판단"인지 단계별로 보여준다.

render_trace()    : 사람이 읽는 단계별 설명 문자열
to_llm_context()  : LLM 추천 엔진에 넘길 구조화 컨텍스트(dict)
"""
from __future__ import annotations

from typing import Dict

from .models import ResourceFeatures
from .pipeline import PipelineTrace

_BLOCKS = "▁▂▃▄▅▆▇█"


def _sparkline(vals) -> str:
    lo, hi = min(vals), max(vals)
    if hi - lo < 1e-9:
        return _BLOCKS[0] * len(vals)
    return "".join(_BLOCKS[min(7, int((v - lo) / (hi - lo) * 8))] for v in vals)


def _fmt_res(name: str, r: ResourceFeatures) -> str:
    return (
        f"  {name:3}  mean={r.mean:5.1f}  p50={r.p50:5.1f}  p90={r.p90:5.1f}  "
        f"p95={r.p95:5.1f}  p99={r.p99:5.1f}  max={r.maximum:5.1f}  "
        f"idle={r.idle_ratio:5.0%}  busy={r.busy_ratio:4.0%}  cv={r.cv:4.2f}"
    )


def render_trace(trace: PipelineTrace) -> str:
    rep = trace.cleaned.report
    L = []
    L.append("=" * 78)
    L.append(f"INSTANCE: {trace.instance_id}   (interval={trace.raw.interval_seconds}s)")
    L.append("=" * 78)

    # ---- STAGE 1 ----
    L.append("[STAGE 1] 데이터 정제 (Cleaning)")
    L.append(f"  raw samples          : {rep.raw_count}")
    L.append(f"  duplicates removed   : {rep.duplicates_removed}")
    L.append(f"  out-of-range clipped : {rep.out_of_range_clipped}")
    L.append(f"  grid slots           : {rep.grid_slots}")
    L.append(f"  real slots           : {rep.real_slots}")
    L.append(f"  short gaps filled    : {rep.short_gaps_filled}")
    L.append(f"  long-gap (missing)   : {rep.long_gap_slots}")
    L.append(f"  coverage             : {rep.coverage:.1%}   →  status: {rep.status}")
    L.append("")

    if trace.features is None:
        L.append("[STAGE 2/3] 건너뜀 — INSUFFICIENT (커버리지 미달로 점수 보류)")
        L.append(f"  ACTION : {trace.score.action.value}")
        return "\n".join(L)

    # ---- STAGE 2 ----
    fv = trace.features
    L.append("[STAGE 2] 피처 추출 (Features)")
    L.append(_fmt_res("CPU", fv.cpu))
    if fv.mem is not None:
        L.append(_fmt_res("MEM", fv.mem))
    L.append(
        f"  → binding(P95) = {fv.binding_resource} {fv.binding_p95:.1f}%   "
        f"binding(P99) = {fv.binding_p99:.1f}%   bound_type = {fv.bound_type}"
    )
    L.append(f"  → trend(binding) = {fv.trend_per_day:+.2f} %/day")
    L.append("")

    # ---- 분석: 하루 모양 (독립 모듈) ----
    if trace.shape is not None:
        sh = trace.shape
        L.append("[분석] 하루 모양 (Daily Shape — 시간대별 프로파일)")
        L.append(f"  0-23h: {_sparkline(sh.profile)}")
        L.append(f"  archetype = {sh.archetype}   peak = {sh.peak_hour:02d}시 ({sh.peak_window})")
        L.append(
            f"  concentration={sh.concentration}  consistency={sh.peak_consistency}  "
            f"spikiness={sh.spikiness}"
        )
        L.append("")

    # ---- STAGE 3 ----
    s = trace.score
    L.append("[STAGE 3] 점수 산출 (RuleScorer)")
    L.append(f"  efficiency_score : {s.efficiency_score:3d} / 100   (높을수록 활용 good = 낭비 적음)")
    L.append(f"  headroom_risk    : {s.headroom_risk:3d} / 100   (높을수록 포화 위험)")
    L.append(f"  >>> ACTION       : {s.action.value.upper()}   (confidence {s.confidence})")
    for r in s.reasons:
        L.append(f"        - {r}")
    return "\n".join(L)


def to_llm_context(trace: PipelineTrace) -> Dict:
    """LLM 추천 엔진 입력용. 점수 + 근거 피처를 함께 실어 환각을 줄인다."""
    s = trace.score
    fv = trace.features
    ctx: Dict = {
        "instance": trace.instance_id,
        "interval_seconds": trace.raw.interval_seconds,
        "window_days": fv.window_days if fv else None,
        "coverage": round(trace.cleaned.report.coverage, 3),
        "efficiency_score": s.efficiency_score,
        "headroom_risk": s.headroom_risk,
        "action_signal": s.action.value,
        "confidence": s.confidence,
        "binding_resource": s.binding_resource,
        "reasons": s.reasons,
    }
    if fv is not None:
        metrics: Dict = {
            "cpu": {
                "p95": round(fv.cpu.p95, 1),
                "p99": round(fv.cpu.p99, 1),
                "mean": round(fv.cpu.mean, 1),
                "idle_ratio": round(fv.cpu.idle_ratio, 3),
            },
        }
        if fv.mem is not None:
            metrics["mem"] = {
                "p95": round(fv.mem.p95, 1),
                "p99": round(fv.mem.p99, 1),
                "mean": round(fv.mem.mean, 1),
                "idle_ratio": round(fv.mem.idle_ratio, 3),
            }
        metrics["trend_per_day"] = round(fv.trend_per_day, 2)
        metrics["bound_type"] = fv.bound_type
        ctx["metrics"] = metrics
    if trace.shape is not None:
        sh = trace.shape
        ctx["daily_shape"] = {
            "method": sh.method,
            "archetype": sh.archetype,
            "peak_hour": sh.peak_hour,
            "peak_window": sh.peak_window,
            "concentration": sh.concentration,
            "peak_consistency": sh.peak_consistency,
            "spikiness": sh.spikiness,
            "profile_24h": sh.profile,
        }
    return ctx
