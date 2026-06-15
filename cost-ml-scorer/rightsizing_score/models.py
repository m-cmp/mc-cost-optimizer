"""
파이프라인 각 단계가 주고받는 데이터 구조.

흐름:  RawMetrics → CleanedMetrics → FeatureVector → InstanceScore
각 단계의 산출물을 별도 타입으로 분리해, "어느 단계에서 무엇이 결정됐는지"를
PipelineTrace 로 그대로 들여다볼 수 있게 한다.
"""
from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime
from enum import Enum
from typing import List, Optional


@dataclass(frozen=True)
class TimeWindow:
    start: datetime
    end: datetime


@dataclass
class MetricSample:
    """수집기/DB에서 넘어온 한 시점의 측정값 (스키마는 datasource 가 흡수).
    mem_pct=None 이면 MEM 미수집(CPU-only)."""
    ts: datetime
    cpu_pct: float
    mem_pct: Optional[float] = None


@dataclass
class RawMetrics:
    instance_id: str
    interval_seconds: int
    samples: List[MetricSample]


# ---------------------------------------------------------------------------
# STAGE 1 산출물
# ---------------------------------------------------------------------------
@dataclass
class CleaningReport:
    raw_count: int = 0
    duplicates_removed: int = 0
    out_of_range_clipped: int = 0
    grid_slots: int = 0           # 윈도우를 interval 로 자른 총 슬롯 수
    real_slots: int = 0           # 실제 측정값이 들어온 슬롯 수
    short_gaps_filled: int = 0    # 보간으로 메운 슬롯 수
    long_gap_slots: int = 0       # 보간 못 하고 결측으로 남긴 슬롯 수
    coverage: float = 0.0         # real_slots / grid_slots
    status: str = "OK"            # OK | INSUFFICIENT
    notes: List[str] = field(default_factory=list)


@dataclass
class CleanedMetrics:
    instance_id: str
    interval_seconds: int
    cpu: List[Optional[float]]    # 그리드에 정렬된 시계열 (결측 = None)
    mem: List[Optional[float]]
    report: CleaningReport


# ---------------------------------------------------------------------------
# STAGE 2 산출물
# ---------------------------------------------------------------------------
@dataclass
class ResourceFeatures:
    mean: float
    p50: float
    p90: float
    p95: float
    p99: float
    maximum: float
    std: float
    cv: float            # 변동계수 = std / mean
    idle_ratio: float    # low_util 미만 시점 비율
    busy_ratio: float    # high_util 초과 시점 비율


@dataclass
class FeatureVector:
    instance_id: str
    coverage: float
    window_days: float
    cpu: ResourceFeatures
    mem: Optional[ResourceFeatures]   # None 이면 CPU-only (MEM 미수집)
    binding_resource: str   # "CPU" | "MEM" — P95 기준 더 빡빡한 축
    binding_p95: float
    binding_p99: float
    binding_busy_ratio: float
    binding_cv: float
    trend_per_day: float    # binding 사용률 추세 (%/day)
    bound_type: str         # cpu-bound | mem-bound | balanced | balanced-idle


@dataclass
class DailyShape:
    """분석 모듈 산출물 — 하루 모양(시간대별 프로파일)."""
    method: str
    profile: List[float]        # 24개 시간대별 binding 값
    archetype: str              # flat | business-hours | night-batch | irregular
    peak_hour: int
    peak_window: str            # 예: "02:00–05:00"
    concentration: float        # 0~1, 소수 시간에 몰린 정도
    peak_consistency: float     # 0~1, 매일 같은 시각에 피크 뜨는 정도
    spikiness: float            # 전체 p99/p50


# ---------------------------------------------------------------------------
# STAGE 3 산출물
# ---------------------------------------------------------------------------
class Action(str, Enum):
    DOWNSIZE = "downsize"
    KEEP = "keep"
    UPSIZE = "upsize"
    INSUFFICIENT = "insufficient_data"


@dataclass
class InstanceScore:
    instance_id: str
    efficiency_score: int   # 0~100, 높을수록 자원 활용 good (= 낭비 적음)
    headroom_risk: int      # 0~100, 높을수록 포화 위험
    action: Action
    confidence: float       # 0~1
    binding_resource: str
    reasons: List[str] = field(default_factory=list)
