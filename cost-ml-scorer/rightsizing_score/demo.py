"""
데모 — 합성 데이터로 파이프라인 전체를 돌려 단계별 산출을 보여준다.

★ 합성 데이터에 '시간대(diurnal) 패턴'을 심어, 하루 모양(shape) 분석이 실제로
   동작하는지 검증할 수 있게 했다. (운영에선 실데이터로 검증해야 함)

실행:  python3 -m rightsizing_score.demo
운영 전환 시 InMemorySource 만 SQLMetricSource(자체 MetricSource)로 교체하면 된다.
"""
from __future__ import annotations

import json
import random
from datetime import datetime, timedelta
from typing import Dict, List

from .config import ScoringConfig
from .datasource import InMemorySource
from .explain import render_trace, to_llm_context
from .models import MetricSample, RawMetrics, TimeWindow
from .pipeline import RightsizingPipeline

# 워크로드 아키타입 (시간대 패턴 포함)
PROFILES: Dict[str, dict] = {
    "i-idle-001":      dict(shape="flat",        cpu=4,  cpu_sd=1.5, mem=10, mem_sd=2, gap_prob=0.005, dirty=True),
    "i-flat-busy-002": dict(shape="flat",        cpu=55, cpu_sd=6,   mem=50, mem_sd=6),
    "i-business-003":  dict(shape="business",    cpu_day=70, cpu_night=15, cpu_sd=3, mem=40, mem_sd=4),
    "i-batch-004":     dict(shape="night-batch", cpu_spike=92, cpu_base=8, spike_h0=2, spike_h1=4, cpu_sd=2, mem=20, mem_sd=3),
    "i-bursty-005":    dict(shape="bursty",      cpu_base=8, cpu_sd=2, spike_prob=0.03, spike=95, mem=20, mem_sd=3),
    "i-sparse-006":    dict(shape="flat",        cpu=6,  cpu_sd=2,   mem=12, mem_sd=2, gap_prob=0.62),
}


def _clamp01(x: float) -> float:
    return max(0.0, min(100.0, x))


def _cpu_level(hour: int, prof: dict) -> float:
    """시각(hour)에 따른 CPU 기준선 — 여기서 하루 모양이 만들어진다."""
    kind = prof["shape"]
    if kind == "business":
        return prof["cpu_day"] if prof_in(hour, 9, 18) else prof["cpu_night"]
    if kind == "night-batch":
        return prof["cpu_spike"] if prof_in(hour, prof["spike_h0"], prof["spike_h1"]) else prof["cpu_base"]
    if kind == "bursty":
        return prof["cpu_base"]            # 스파이크는 시각 무관 랜덤(아래)
    return prof["cpu"]                      # flat


def prof_in(hour: int, lo: int, hi: int) -> bool:
    return lo <= hour <= hi


def _gen(iid: str, prof: dict, cfg: ScoringConfig, rng: random.Random, start: datetime) -> RawMetrics:
    interval = cfg.interval_seconds
    n = int(cfg.window_days * 86400 // interval) + 1
    samples: List[MetricSample] = []
    for i in range(n):
        if rng.random() < prof.get("gap_prob", 0.0):
            continue
        dt = start + timedelta(seconds=i * interval)
        cpu = rng.gauss(_cpu_level(dt.hour, prof), prof.get("cpu_sd", 3))
        if prof["shape"] == "bursty" and rng.random() < prof.get("spike_prob", 0.0):
            cpu = rng.gauss(prof["spike"], 2)   # 시각 무관 랜덤 스파이크
        mem = rng.gauss(prof.get("mem", 20), prof.get("mem_sd", 3))
        samples.append(MetricSample(dt, _clamp01(cpu), _clamp01(mem)))

    if prof.get("dirty"):   # 정제 단계 시연용 더티 데이터
        samples.append(MetricSample(start + timedelta(seconds=10 * interval), 103.0, 9.0))  # 범위초과
        samples.append(MetricSample(start + timedelta(seconds=20 * interval), 5.0, -3.0))   # 음수
        dup = start + timedelta(seconds=30 * interval)
        samples.append(MetricSample(dup, 4.0, 10.0))
        samples.append(MetricSample(dup, 4.2, 10.1))                                        # 중복 ts

    return RawMetrics(iid, interval, samples)


def main() -> None:
    cfg = ScoringConfig(interval_seconds=300, window_days=14.0)   # 5분 주기, 14일
    rng = random.Random(42)
    start = datetime(2026, 5, 20, 0, 0, 0)
    n = int(cfg.window_days * 86400 // cfg.interval_seconds)
    window = TimeWindow(start=start, end=start + timedelta(seconds=n * cfg.interval_seconds))

    data = {iid: _gen(iid, prof, cfg, rng, start) for iid, prof in PROFILES.items()}
    pipeline = RightsizingPipeline(source=InMemorySource(data), config=cfg)
    traces = pipeline.run_all(window)

    for t in traces:
        print(render_trace(t))
        print()

    print("=" * 78)
    print("SUMMARY")
    print("=" * 78)
    print(f"{'instance':16} {'action':14} {'shape':14} {'eff':>4} {'risk':>5} {'conf':>5}")
    print("-" * 78)
    for t in traces:
        s = t.score
        shp = t.shape.archetype if t.shape else "-"
        print(f"{t.instance_id:16} {s.action.value:14} {shp:14} {s.efficiency_score:>4} "
              f"{s.headroom_risk:>5} {s.confidence:>5}")

    print("\n" + "=" * 78)
    print("LLM CONTEXT 예시 (i-batch-004 → LLM 추천 엔진 입력)")
    print("=" * 78)
    batch = next(t for t in traces if t.instance_id == "i-batch-004")
    print(json.dumps(to_llm_context(batch), ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
