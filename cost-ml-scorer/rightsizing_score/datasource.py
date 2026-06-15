"""
MetricSource — 데이터 소스 추상화.

- InMemorySource          : 메모리 적재 (테스트 / 이미 fetch 한 데이터 재사용)
- AssetComputeMetricSource: cost.asset_compute_metric (CPU 전용) 읽기 전용 MySQL/MariaDB 어댑터

수집은 다른 서비스가 하고, 우리는 DB만 읽는다. 수집 간격조차 데이터에서 추론한다.
"""
from __future__ import annotations

from abc import ABC, abstractmethod
from datetime import datetime
from typing import Dict, List, Optional

from .models import MetricSample, RawMetrics, TimeWindow


class MetricSource(ABC):
    @abstractmethod
    def list_instances(self) -> List[str]:
        ...

    @abstractmethod
    def fetch(self, instance_id: str, window: TimeWindow) -> RawMetrics:
        ...


class InMemorySource(MetricSource):
    """미리 만들어 둔 RawMetrics 제공 (테스트, 또는 이미 fetch 한 데이터 재사용)."""

    def __init__(self, data: Dict[str, RawMetrics]):
        self._data = data

    def list_instances(self) -> List[str]:
        return list(self._data.keys())

    def fetch(self, instance_id: str, window: TimeWindow) -> RawMetrics:
        rm = self._data[instance_id]
        sel = [s for s in rm.samples if window.start <= s.ts <= window.end]
        return RawMetrics(instance_id, rm.interval_seconds, sel)


def _infer_interval_seconds(timestamps: List[datetime], default: int = 300) -> int:
    """연속 timestamp 간격의 중앙값(초). 수집 간격을 데이터에서 추론한다."""
    ts = sorted(set(timestamps))
    if len(ts) < 2:
        return default
    diffs = sorted((ts[i + 1] - ts[i]).total_seconds() for i in range(len(ts) - 1))
    med = diffs[len(diffs) // 2]
    return int(med) if med >= 1 else default


class AssetComputeMetricSource(MetricSource):
    """
    cost.asset_compute_metric (long 포맷) 읽기 전용 — CPU 전용.

    metric_type='cpu' 행만 읽어 cpu_pct 로 싣고, mem_pct 는 None(미수집).
    수집 간격은 collect_dt 들에서 자동 추론.
    매핑: instance=csp_instanceid, ts=collect_dt, cpu=metric_amount
    """

    def __init__(self, conn, *, table: str = "cost.asset_compute_metric",
                 instances: Optional[List[str]] = None):
        self._conn = conn
        self.table = table
        self._instances = instances

    def list_instances(self) -> List[str]:
        if self._instances is not None:
            return self._instances
        cur = self._conn.cursor()
        cur.execute(f"SELECT DISTINCT csp_instanceid FROM {self.table} WHERE metric_type='cpu'")
        return [r[0] for r in cur.fetchall()]

    def fetch(self, instance_id: str, window: TimeWindow) -> RawMetrics:
        cur = self._conn.cursor()
        cur.execute(
            f"SELECT collect_dt, metric_amount FROM {self.table} "
            f"WHERE csp_instanceid=%s AND metric_type='cpu' "
            f"AND collect_dt BETWEEN %s AND %s ORDER BY collect_dt",
            (instance_id, window.start, window.end),
        )
        rows = cur.fetchall()
        samples = [MetricSample(ts=r[0], cpu_pct=float(r[1]), mem_pct=None) for r in rows]
        interval = _infer_interval_seconds([s.ts for s in samples])
        return RawMetrics(instance_id, interval, samples)
