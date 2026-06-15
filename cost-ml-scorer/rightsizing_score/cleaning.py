"""
STAGE 1 — 데이터 정제.

원시 메트릭을 '고정 간격 그리드'에 정렬하고, 결측/중복/이상치를 처리한다.
설계 원칙:
  * 실제 피크(P95/P99)는 신호이므로 절대 평활하지 않는다. (업사이즈 판단의 근거)
  * 짧은 결측만 보간하고, 긴 결측은 결측으로 남겨 피처 계산에서 제외한다.
  * 커버리지가 낮으면 점수를 내지 않고 INSUFFICIENT 로 보류한다 (틀린 추천 방지).
모든 처리 내역은 CleaningReport 에 남겨 추적 가능하게 한다.
"""
from __future__ import annotations

from typing import List, Optional

from .config import ScoringConfig
from .models import CleanedMetrics, CleaningReport, RawMetrics, TimeWindow


def _fill_short_gaps(arr: List[Optional[float]], max_gap: int) -> int:
    """연속 결측 구간 길이가 max_gap 이하면 선형보간(가장자리는 양끝 값으로 채움)."""
    n = len(arr)
    filled = 0
    i = 0
    while i < n:
        if arr[i] is not None:
            i += 1
            continue
        j = i
        while j < n and arr[j] is None:
            j += 1
        gap_len = j - i
        if gap_len <= max_gap:
            prev = arr[i - 1] if i - 1 >= 0 else None
            nxt = arr[j] if j < n else None
            if prev is None and nxt is None:
                pass  # 전체가 결측 — 채울 근거 없음
            elif prev is None:
                for k in range(i, j):
                    arr[k] = nxt
                    filled += 1
            elif nxt is None:
                for k in range(i, j):
                    arr[k] = prev
                    filled += 1
            else:
                step = (nxt - prev) / (gap_len + 1)
                for k in range(i, j):
                    arr[k] = prev + step * (k - i + 1)
                    filled += 1
        i = j
    return filled


def clean(raw: RawMetrics, window: TimeWindow, config: ScoringConfig) -> CleanedMetrics:
    rep = CleaningReport(raw_count=len(raw.samples))
    interval = raw.interval_seconds

    # 1) 시간순 정렬 + 같은 timestamp 중복 제거(마지막 값 유지)
    by_ts = {}
    for s in sorted(raw.samples, key=lambda x: x.ts):
        if s.ts in by_ts:
            rep.duplicates_removed += 1
        by_ts[s.ts] = s
    samples = list(by_ts.values())

    # 2) 유효범위 밖 값 클립 (예: 음수, 100% 초과). mem 은 미수집(None)일 수 있음.
    for s in samples:
        c = min(max(s.cpu_pct, config.value_min), config.value_max)
        if c != s.cpu_pct:
            rep.out_of_range_clipped += 1
        s.cpu_pct = c
        if s.mem_pct is not None:
            m = min(max(s.mem_pct, config.value_min), config.value_max)
            if m != s.mem_pct:
                rep.out_of_range_clipped += 1
            s.mem_pct = m

    # 3) 고정 간격 그리드에 배치
    total_secs = (window.end - window.start).total_seconds()
    n = int(total_secs // interval) + 1
    cpu: List[Optional[float]] = [None] * n
    mem: List[Optional[float]] = [None] * n
    for s in samples:
        idx = round((s.ts - window.start).total_seconds() / interval)
        if 0 <= idx < n:
            if cpu[idx] is None:
                rep.real_slots += 1
            cpu[idx] = s.cpu_pct
            mem[idx] = s.mem_pct
    rep.grid_slots = n
    rep.coverage = rep.real_slots / n if n else 0.0

    # 4) 짧은 결측 보간 (긴 결측은 그대로 둔다)
    rep.short_gaps_filled = _fill_short_gaps(cpu, config.max_gap_intervals)
    _fill_short_gaps(mem, config.max_gap_intervals)

    # 5) (선택) 워밍업 구간 제외 — 부팅/배포 직후 스파이크 배제
    if config.warmup_skip_intervals > 0:
        for k in range(min(config.warmup_skip_intervals, n)):
            cpu[k] = mem[k] = None
        rep.notes.append(f"warmup {config.warmup_skip_intervals} slots excluded")

    rep.long_gap_slots = sum(1 for v in cpu if v is None)

    # 6) 커버리지 게이트
    if rep.coverage < config.min_coverage:
        rep.status = "INSUFFICIENT"
        rep.notes.append(
            f"coverage {rep.coverage:.1%} < min {config.min_coverage:.0%} → 점수 보류"
        )

    return CleanedMetrics(raw.instance_id, interval, cpu, mem, rep)
