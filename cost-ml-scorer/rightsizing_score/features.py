"""
STAGE 2 — 피처 엔지니어링.

정제된 시계열을 'instance 1개 = 피처 벡터 1개'로 압축한다.
right-sizing 판단의 핵심 신호:
  * 꼬리값(P95/P99) : 얼마나 자주/높이 치솟는가 → 업사이즈 여부
  * idle_ratio      : 얼마나 자주 노는가          → 다운사이즈 여부
  * binding(P95)    : CPU/MEM 중 더 빡빡한 축      → 다운사이즈를 막는 기준
  * trend_per_day   : 추세                         → 선제 대응/보류
"""
from __future__ import annotations

import statistics
from typing import List, Optional

from .config import ScoringConfig
from .models import CleanedMetrics, FeatureVector, ResourceFeatures


def _percentile(sorted_vals: List[float], q: float) -> float:
    """q: 0~100, 선형보간 방식."""
    if not sorted_vals:
        return float("nan")
    if len(sorted_vals) == 1:
        return sorted_vals[0]
    rank = (q / 100.0) * (len(sorted_vals) - 1)
    lo = int(rank)
    hi = min(lo + 1, len(sorted_vals) - 1)
    frac = rank - lo
    return sorted_vals[lo] * (1 - frac) + sorted_vals[hi] * frac


def _resource_features(vals: List[float], config: ScoringConfig) -> ResourceFeatures:
    sv = sorted(vals)
    mean = statistics.fmean(vals)
    std = statistics.pstdev(vals) if len(vals) > 1 else 0.0
    cv = std / mean if mean > 1e-9 else 0.0
    n = len(vals)
    idle = sum(1 for v in vals if v < config.low_util) / n
    busy = sum(1 for v in vals if v > config.high_util) / n
    return ResourceFeatures(
        mean=mean,
        p50=_percentile(sv, 50),
        p90=_percentile(sv, 90),
        p95=_percentile(sv, 95),
        p99=_percentile(sv, 99),
        maximum=sv[-1],
        std=std,
        cv=cv,
        idle_ratio=idle,
        busy_ratio=busy,
    )


def _trend_per_day(series: List[float], interval_seconds: int) -> float:
    """단순 최소제곱 기울기(per interval)를 %/day 로 환산."""
    n = len(series)
    if n < 2:
        return 0.0
    xbar = (n - 1) / 2.0
    ybar = statistics.fmean(series)
    num = sum((i - xbar) * (series[i] - ybar) for i in range(n))
    den = sum((i - xbar) ** 2 for i in range(n))
    slope = num / den if den else 0.0
    return slope * (86400.0 / interval_seconds)


def _bound_type(cpu: ResourceFeatures, mem: ResourceFeatures, config: ScoringConfig) -> str:
    c, m = cpu.p95, mem.p95
    if c < config.low_util and m < config.low_util:
        return "balanced-idle"
    if c >= config.high_util and m < config.high_util:
        return "cpu-bound"
    if m >= config.high_util and c < config.high_util:
        return "mem-bound"
    if abs(c - m) < 10:
        return "balanced"
    return "cpu-bound" if c > m else "mem-bound"


def extract_features(cleaned: CleanedMetrics, config: ScoringConfig) -> Optional[FeatureVector]:
    if cleaned.report.status == "INSUFFICIENT":
        return None
    cpu_vals = [v for v in cleaned.cpu if v is not None]
    mem_vals = [v for v in cleaned.mem if v is not None]
    if not cpu_vals:
        return None

    cpu = _resource_features(cpu_vals, config)
    mem = _resource_features(mem_vals, config) if mem_vals else None

    if mem is not None:
        binding_resource = "CPU" if cpu.p95 >= mem.p95 else "MEM"
        binding_p95 = max(cpu.p95, mem.p95)
        binding_p99 = max(cpu.p99, mem.p99)
        binding_busy = max(cpu.busy_ratio, mem.busy_ratio)
        binding_cv = cpu.cv if binding_resource == "CPU" else mem.cv
        bound_type = _bound_type(cpu, mem, config)
        # 추세: 두 자원의 동시점 최댓값(그 시점 가장 빡빡한 축) 시계열
        series = [max(c, m) for c, m in zip(cleaned.cpu, cleaned.mem)
                  if c is not None and m is not None]
    else:
        # CPU-only: MEM 미수집 → binding 은 CPU 그대로
        binding_resource = "CPU"
        binding_p95, binding_p99 = cpu.p95, cpu.p99
        binding_busy, binding_cv = cpu.busy_ratio, cpu.cv
        bound_type = "cpu-only"
        series = [c for c in cleaned.cpu if c is not None]

    trend = _trend_per_day(series, cleaned.interval_seconds)

    return FeatureVector(
        instance_id=cleaned.instance_id,
        coverage=cleaned.report.coverage,
        window_days=config.window_days,
        cpu=cpu,
        mem=mem,
        binding_resource=binding_resource,
        binding_p95=binding_p95,
        binding_p99=binding_p99,
        binding_busy_ratio=binding_busy,
        binding_cv=binding_cv,
        trend_per_day=trend,
        bound_type=bound_type,
    )
