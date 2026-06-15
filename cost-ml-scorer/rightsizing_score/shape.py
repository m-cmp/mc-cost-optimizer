"""
분석 모듈 — 하루 모양(Daily Shape), 시간대별 프로파일 방식.

함수 모양(선형/시그모이드 등)을 가정하지 않는다. 데이터를 '시각(0~23시)별'로
묶어 전형적인 하루 곡선을 만들고, 거기서 해석 가능한 피처를 뽑아 archetype 으로 분류한다.

- binding 시계열(각 시점 max(cpu, mem))의 시간대별 통계로 곡선 생성
- archetype: flat | business-hours | night-batch | irregular
  (flat 이지만 전체적으로 튀면 → irregular = 시간 국소화 안 된 랜덤 스파이크)

※ 이 모듈은 features/scoring 과 독립이다 (입력은 cleaned + window). → 병렬 실행 가능.
"""
from __future__ import annotations

import statistics
from datetime import timedelta
from typing import List, Optional

from .config import ScoringConfig
from .features import _percentile
from .models import CleanedMetrics, DailyShape, TimeWindow


def _classify(peak_to_mean: float, peak_hour: int, consistency: float,
              concentration: float, spikiness: float, c: ScoringConfig) -> str:
    # 1) 평탄 프로파일?
    if peak_to_mean < c.shape_flat_peak_ratio:
        # 평탄해도 전체적으로 튀면(시간 국소화 안 된 랜덤 스파이크) → irregular
        return "irregular" if spikiness >= c.shape_spiky_ratio else "flat"
    # 2) 시간 국소화된 피크
    night = c.shape_night_start <= peak_hour <= c.shape_night_end
    day = c.shape_business_start <= peak_hour <= c.shape_business_end
    if night and concentration >= c.shape_batch_concentration:
        return "night-batch"
    if day:
        return "business-hours"
    return "irregular"


def analyze_shape(cleaned: CleanedMetrics, window: TimeWindow,
                  config: ScoringConfig) -> Optional[DailyShape]:
    if cleaned.report.status == "INSUFFICIENT":
        return None

    interval = cleaned.interval_seconds
    has_mem = any(m is not None for m in cleaned.mem)
    hourly: List[List[float]] = [[] for _ in range(24)]
    per_day_peak = {}      # day -> (max_binding, hour)
    binding_all: List[float] = []

    for i, (cpu, mem) in enumerate(zip(cleaned.cpu, cleaned.mem)):
        if cpu is None:
            continue
        b = cpu if mem is None else max(cpu, mem)   # CPU-only면 cpu
        binding_all.append(b)
        dt = window.start + timedelta(seconds=i * interval)
        hourly[dt.hour].append(b)
        dk = dt.toordinal()
        if dk not in per_day_peak or b > per_day_peak[dk][0]:
            per_day_peak[dk] = (b, dt.hour)

    if not binding_all:
        return None

    overall_mean = statistics.fmean(binding_all)
    profile = [statistics.fmean(hourly[h]) if hourly[h] else overall_mean for h in range(24)]

    pmax = max(profile)
    pmean = statistics.fmean(profile)
    peak_hour = max(range(24), key=lambda h: profile[h])
    peak_to_mean = pmax / pmean if pmean > 1e-9 else 1.0

    # 집중도: 상위 N시간이 전체 합에서 차지하는 비중
    total = sum(profile)
    top_n = sorted(profile, reverse=True)[: config.shape_concentration_top_n]
    concentration = sum(top_n) / total if total > 1e-9 else 0.0

    # 피크 일관성: 매일의 피크 시각이 한 시각(±1h)에 몰리나
    peak_hours = [hr for (_, hr) in per_day_peak.values()]
    if peak_hours:
        mode_h = max(set(peak_hours), key=peak_hours.count)
        consistency = sum(1 for hr in peak_hours if abs(hr - mode_h) <= 1) / len(peak_hours)
    else:
        consistency = 0.0

    # 전체 스파이크성: p99 / p50
    sv = sorted(binding_all)
    p50 = _percentile(sv, 50)
    p99 = _percentile(sv, 99)
    spikiness = p99 / p50 if p50 > 1e-9 else float("inf")

    # 상승(elevated) 구간 → peak_window 표기
    thr = pmean + 0.5 * (pmax - pmean)
    elevated = [h for h in range(24) if profile[h] >= thr]
    peak_window = f"{min(elevated):02d}:00–{max(elevated) + 1:02d}:00" if elevated else "—"

    archetype = _classify(peak_to_mean, peak_hour, consistency, concentration, spikiness, config)

    return DailyShape(
        method=f"hour-of-day mean profile (24 buckets, binding={'max(cpu,mem)' if has_mem else 'cpu'})",
        profile=[round(x, 1) for x in profile],
        archetype=archetype,
        peak_hour=peak_hour,
        peak_window=peak_window,
        concentration=round(concentration, 2),
        peak_consistency=round(consistency, 2),
        spikiness=round(spikiness, 2),
    )
