"""
파이프라인 오케스트레이션.

source.fetch → clean → extract_features → scorer.score 를 순서대로 묶고,
각 단계의 '중간 산출물'을 PipelineTrace 에 그대로 담아 추적 가능하게 한다.
"""
from __future__ import annotations

from dataclasses import dataclass
from typing import List, Optional

from .cleaning import clean
from .config import ScoringConfig
from .datasource import MetricSource
from .features import extract_features
from .models import (
    Action,
    CleanedMetrics,
    DailyShape,
    FeatureVector,
    InstanceScore,
    RawMetrics,
    TimeWindow,
)
from .scoring import RuleScorer, Scorer
from .shape import analyze_shape


@dataclass
class PipelineTrace:
    """한 instance가 파이프라인을 통과한 전체 기록."""
    instance_id: str
    window: TimeWindow
    raw: RawMetrics
    cleaned: CleanedMetrics
    features: Optional[FeatureVector]
    shape: Optional[DailyShape]
    score: InstanceScore


class RightsizingPipeline:
    def __init__(
        self,
        source: MetricSource,
        config: Optional[ScoringConfig] = None,
        scorer: Optional[Scorer] = None,
    ):
        self.source = source
        self.config = config or ScoringConfig()
        self.scorer = scorer or RuleScorer(self.config)

    def run(self, instance_id: str, window: TimeWindow) -> PipelineTrace:
        raw = self.source.fetch(instance_id, window)
        cleaned = clean(raw, window, self.config)
        features = extract_features(cleaned, self.config)
        shape = analyze_shape(cleaned, window, self.config)   # features/score와 독립

        if features is None:
            note = cleaned.report.notes[0] if cleaned.report.notes else "데이터 부족"
            score = InstanceScore(
                instance_id=instance_id,
                efficiency_score=0,
                headroom_risk=0,
                action=Action.INSUFFICIENT,
                confidence=0.0,
                binding_resource="-",
                reasons=[note],
            )
        else:
            score = self.scorer.score(features)

        return PipelineTrace(instance_id, window, raw, cleaned, features, shape, score)

    def run_all(self, window: TimeWindow) -> List[PipelineTrace]:
        return [self.run(i, window) for i in self.source.list_instances()]
